package org.memoq_for_edt.internal;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

public class UnloadSourceLangDictionaries extends AbstractHandler
{

    private final String CONTEXT_DICTIONARY_EXTENSION = Messages.ContextDictionaryExtension;

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        try
        {
            UnloadDictionaries(event);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public void UnloadDictionaries(ExecutionEvent event) throws IOException
    {
        IStructuredSelection selection = HandlerUtil.getCurrentStructuredSelection(event);
        if (selection.isEmpty()) {
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
		    Files.move(file, target.resolve(source.relativize(file).toString().replace(
				    "_en.".concat(CONTEXT_DICTIONARY_EXTENSION),
				    "_ro.".concat(CONTEXT_DICTIONARY_EXTENSION))),
			    options);
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
