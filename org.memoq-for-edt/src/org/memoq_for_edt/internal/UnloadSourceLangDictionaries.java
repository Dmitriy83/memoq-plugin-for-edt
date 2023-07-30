package org.memoq_for_edt.internal;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.progress.IProgressService;

public class UnloadSourceLangDictionaries extends AbstractHandler
{

    private final String CONTEXT_DICTIONARY_EXTENSION = Messages.ContextDictionaryExtension;

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        try {
            IWorkbench wb = PlatformUI.getWorkbench();
            IProgressService ps = wb.getProgressService();
            ps.busyCursorWhile(new IRunnableWithProgress() {
               @Override
                public void run(IProgressMonitor pm) {
                    try {
                        unloadDictionaries(event);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void unloadDictionaries(ExecutionEvent event) throws IOException
    {
        IStructuredSelection selection = HandlerUtil.getCurrentStructuredSelection(event);
        if (selection.isEmpty())
        {
            return;
        }

        Object project = selection.getFirstElement();
        if (!(project instanceof IProject))
        {
            return;
        }

        IProject adapter = ((IProject)project).getAdapter(IProject.class);
        IPath projectLocation = adapter.getLocation();

        Path srcSourceDirectory = Path.of(projectLocation.append(Messages.src_source).toOSString());
        if (!Files.exists(srcSourceDirectory))
        {
            return;
        }

        String srcTargetDirectoryStr = projectLocation.append(Messages.src_target).toOSString();
        Path srcTargetDirectory = Path.of(srcTargetDirectoryStr);
        if (Files.exists(srcTargetDirectory))
        {
            FileUtils.deleteDirectory(new File(srcTargetDirectoryStr));
        }

        copyFolder(srcSourceDirectory, srcTargetDirectory, StandardCopyOption.REPLACE_EXISTING);

        // Open file explorer
        File file = new File(srcTargetDirectoryStr);
        Desktop desktop = Desktop.getDesktop();
        desktop.open(file);
    }

    public void copyFolder(Path source, Path target, CopyOption options) throws IOException
    {

        Files.walkFileTree(source, new SimpleFileVisitor<Path>()
        {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException
            {
                Files.createDirectories(target.resolve(source.relativize(dir).toString()));
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
            {
                if (CONTEXT_DICTIONARY_EXTENSION.equals(FilenameUtils.getExtension(file.toString())))
                {
                    fixKeys(file);
                    Files.move(file, target.resolve(source.relativize(file).toString()
                            .replace(Messages.filterSourceLanguage.concat(CONTEXT_DICTIONARY_EXTENSION),
                                Messages.filterTargetLanguage.concat(CONTEXT_DICTIONARY_EXTENSION))),
                        options);
                }
                return FileVisitResult.CONTINUE;
            }

            private void fixKeys(Path file) throws IOException
            {
                List<String> fileContent = new ArrayList<>(Files.readAllLines(file, StandardCharsets.UTF_8));
                String lineText;

                boolean wasFileModified = false;

                for (int i = 0; i < fileContent.size(); i++)
                {
                    lineText = fileContent.get(i);
                    String lineTextFixed = fixKeyInLine(lineText);
                    if (!lineText.equals(lineTextFixed))
                    {
                        wasFileModified = true;
                        fileContent.set(i, lineTextFixed);
                    }
                }

                if (wasFileModified)
                {
                    Files.write(file, fileContent, StandardCharsets.UTF_8);
                }
            }
        });
    }

    @SuppressWarnings("nls")
    protected String fixKeyInLine(String text)
    {
        // Find index of the first entry "=" in the text line (if it's not preceded by "\")
        Pattern regexPattern = Pattern.compile("(?<!\\\\)\\=");
        Matcher keyMatch = regexPattern.matcher(text);

        boolean wasLineModified = false;

        if (keyMatch.find())
        {
            int keyValueSeparatorIndex = keyMatch.start();
            String key = StringUtils.left(text, keyValueSeparatorIndex);

            // Find entries of spaces or non-breaking spaces in the key
            regexPattern = Pattern.compile("(?<!\\\\)\\xa0|(?<!\\\\)\\x20");
            Matcher wordsSeparatorMatch = regexPattern.matcher(key);
            while (wordsSeparatorMatch.find()) {
                int index = wordsSeparatorMatch.start();
                String prevSymbol = StringUtils.mid(key, index - 1, 1);
                if (!(prevSymbol == "\\" || prevSymbol == " " || prevSymbol == Character.toString((char)160))) {
                    wasLineModified = true;
                    key = StringUtils.left(key, index).concat("\\").concat(StringUtils.right(key, key.length() - index));
                }
            }

            // Generate result text line with fixed key
            if (wasLineModified) {
                text = key.concat(StringUtils.right(text, text.length() - keyValueSeparatorIndex));
            }
        }

        return text;
    }
}
