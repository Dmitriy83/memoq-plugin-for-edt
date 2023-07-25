package org.memoq_for_edt.internal;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.memoq-for-edt"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

    private Injector injector;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

    /**
     * Returns Guice injector for this plugin.
     *
     * @return Guice injector for this plugin, never {@code null}
     */
    public synchronized Injector getInjector()
    {
        if (injector == null)
            return injector = createInjector();
        return injector;
    }

    private Injector createInjector()
    {
        try
        {
            return Guice.createInjector(new ExternalDependenciesModule(this));
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to create injector for " + getBundle().getSymbolicName(), e); //$NON-NLS-1$
        }
    }

}
