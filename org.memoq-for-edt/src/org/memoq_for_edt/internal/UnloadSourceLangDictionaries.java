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
import java.util.Scanner;
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

        String scriptLanguage = getScriptLanguage(srcSourceDirectory);
        if (scriptLanguage.equals("Russian")) { //$NON-NLS-1$
            Messages.src_target = "src_en"; //$NON-NLS-1$
            Messages.filterSourceLanguage = "_ru."; //$NON-NLS-1$
            Messages.filterTargetLanguage = "_en."; //$NON-NLS-1$
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

        if (keyMatch.find())
        {
            int keyValueSeparatorIndex = keyMatch.start();
            String keyInitial   = StringUtils.left(text, keyValueSeparatorIndex);
            String keyFixed     = keyInitial
                                    .replaceAll("(?<!(\\\\|\\xa0|\\x20))\\xa0", "\\\\".concat(Character.toString((char)160)))
                                    .replaceAll("(?<!(\\\\|\\xa0|\\x20))\\x20", "\\\\ ");

            // Generate result text line with fixed key
            if (!keyInitial.equals(keyFixed)) {
                text = keyFixed.concat(StringUtils.right(text, text.length() - keyValueSeparatorIndex));
            }
        }

        return text;
    }

    private String getScriptLanguage(Path srcSourceDirectory)
    {
        String scriptLanguage = Messages.defaultScriptLanguage;

        Path configurationFile = Path.of(srcSourceDirectory.toString().concat("\\\\Configuration\\\\Configuration.mdo")); //$NON-NLS-1$
        if (Files.exists(configurationFile))
        {
            Scanner scanner;
            try
            {
                scanner = new Scanner(configurationFile);
                int index1 = -1; int index2 = -1;
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    index1 = line.indexOf("<scriptVariant>"); //$NON-NLS-1$
                    if (index1 != -1) {
                        index2 = line.indexOf("</scriptVariant>"); //$NON-NLS-1$
                        if (index2 != -1) {
                            scriptLanguage = line.substring(index1 + 15, index2);
                            break;
                        }
                    }
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return scriptLanguage;
    }
}
