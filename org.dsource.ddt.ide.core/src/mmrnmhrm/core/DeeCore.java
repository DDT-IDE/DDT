package mmrnmhrm.core;

import melnorme.lang.ide.core.LangCore;
import mmrnmhrm.core.projectmodel.DubModelManager;

import org.eclipse.core.runtime.CoreException;
import org.osgi.framework.BundleContext;

/**
 * Singleton class for D IDE Core.
 */
public class DeeCore extends LangCore {
	
	public static final String PLUGIN_ID = "org.dsource.ddt.ide.core";
	public static final String TESTS_PLUGIN_ID = PLUGIN_ID + ".tests";
	
	protected static DeeCore pluginInstance;
	
	/** Returns the shared instance. */
	public static LangCore getInstance() {
		return pluginInstance;
	}
	
	@Override
	public void start(BundleContext context) throws Exception {
		pluginInstance = this;
		super.start(context);
		initPlugin();
	}
	
	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		DubModelManager.shutdownDefault();
		pluginInstance = null;
	}
	
	
	public void initPlugin() throws CoreException {
		// Note: the core plugin does not start the DubModelManager... it is the responsiblity of
		// the Dee UI plugin (or some other "application" code) to start it, 
		// so that they can register listeners first.
		//DubModelManager.startDefault();
	}
	
}