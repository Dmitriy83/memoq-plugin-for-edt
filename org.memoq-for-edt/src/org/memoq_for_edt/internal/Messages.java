/**
 *
 */
package org.memoq_for_edt.internal;

import org.eclipse.osgi.util.NLS;

/**
 * @author dzhih
 *
 */
public class Messages extends NLS {
    private static final String BUNDLE_NAME = Messages.class.getPackageName() + ".messages"; //$NON-NLS-1$
    public static String ContextDictionaryExtension;
    public static String src_source;
    public static String src_target;
    static {
	// initialize resource bundle
	NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
