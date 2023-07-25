/**
 *
 */
package org.memoq_for_edt.internal;

import org.eclipse.core.runtime.Plugin;

import com._1c.g5.v8.dt.core.platform.IBmModelManager;
import com._1c.g5.v8.dt.core.platform.IConfigurationProvider;
import com._1c.g5.wiring.AbstractServiceAwareModule;

/**
 * @author dzhih
 *
 */
public class ExternalDependenciesModule
    extends AbstractServiceAwareModule
{
    /**
     * Constructor of {@link MyPluginExternalDependenciesModule}.
     *
     * @param bundle the parent bundle, cannot be {@code null}
     */
    public ExternalDependenciesModule(Plugin bundle)
    {
        super(bundle);
    }

    @Override
    protected void doConfigure()
    {
        // Связываем сервис общего назначения IConfigurationProvider
        bind(IConfigurationProvider.class).toService();

        bind(IBmModelManager.class).toService();
    }

}
