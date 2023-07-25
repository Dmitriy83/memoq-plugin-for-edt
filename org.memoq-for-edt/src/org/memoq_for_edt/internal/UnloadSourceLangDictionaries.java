package org.memoq_for_edt.internal;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com._1c.g5.v8.bm.core.IBmTransaction;
import com._1c.g5.v8.bm.integration.AbstractBmTask;
import com._1c.g5.v8.bm.integration.IBmModel;
import com._1c.g5.v8.dt.core.platform.IBmModelManager;
import com._1c.g5.v8.dt.core.platform.IConfigurationProvider;
import com._1c.g5.v8.dt.metadata.mdclass.Configuration;
import com.google.inject.Inject;

public class UnloadSourceLangDictionaries
    extends AbstractHandler
{
    private static final String FIRST_VERSION = "1.0.1.0";

    @Inject
    private IConfigurationProvider configurationProvider;

    @Inject
    private IBmModelManager bmModelManager;

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        IStructuredSelection selection = HandlerUtil.getCurrentStructuredSelection(event);
        if (selection.isEmpty()) {
            return null;
        }

        Object project = selection.getFirstElement();
        if (!(project instanceof IProject))
        {
            return null;
        }

        Configuration configuration = configurationProvider.getConfiguration((IProject)project);
        String version = configuration.getVersion();
        if (version == null || version.isEmpty())
        {
            version = FIRST_VERSION;
        }

        String[] serments = version.split("\\.");
        if (serments.length <= 1)
        {
            return null;
        }

        String lastSegment = serments[serments.length - 1];
        int qualiffier;
        try
        {
            qualiffier = Integer.parseInt(lastSegment);
        }
        catch (Exception e)
        {
            qualiffier = 0;
        }

        qualiffier++;
        serments[serments.length - 1] = String.valueOf(qualiffier);
        final String newVersion = String.join(".", serments);

        IBmModel model = bmModelManager.getModel((IProject)project);
        if (model == null)
        {
            return null;
        }

        model.getGlobalContext().execute(new AbstractBmTask<Void>("Unload source language dictionaries")
        {
            @Override
            public Void execute(IBmTransaction transaction, IProgressMonitor monitor)
            {
                Configuration editing = transaction.toTransactionObject(configuration);
                editing.setVersion(newVersion);

                return null;
            }
        });

        return null;
    }
}
