/**
 *
 */
package org.memoq_for_edt.internal;

import org.osgi.framework.Bundle;

import com._1c.g5.wiring.AbstractGuiceAwareExecutableExtensionFactory;
import com.google.inject.Injector;

/**
 * @author dzhih
 *
 */
public class PluginExecutableExtensionFactory
    extends AbstractGuiceAwareExecutableExtensionFactory
{

    @Override
    protected Bundle getBundle()
    {
        return Activator.getDefault().getBundle();
    }

    @Override
    protected Injector getInjector()
    {
        return Activator.getDefault().getInjector();
    }

}
