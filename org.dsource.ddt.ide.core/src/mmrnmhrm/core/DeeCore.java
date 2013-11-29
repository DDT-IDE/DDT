package mmrnmhrm.core;

import mmrnmhrm.core.projectmodel.DeeProjectModel;
import mmrnmhrm.core.projectmodel.DubProjectModel;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.BundleContext;

/**
 * Singleton class for D IDE Core.
 */
public class DeeCore extends LangCore {
	
	public static final String PLUGIN_ID = "org.dsource.ddt.ide.core";
	public static final String TESTS_PLUGIN_ID = PLUGIN_ID + ".tests";
	// The convention for the id prefix for extensions contributed by this plugin
	public static final String EXTENSIONS_IDPREFIX = PLUGIN_ID + "."; 

	protected static DeeCore pluginInstance;
	
	/** Returns the shared instance. */
	public static LangCore getInstance() {
		return pluginInstance;
	}
	
	public static final boolean DEBUG_MODE = "true".equalsIgnoreCase(Platform.getDebugOption(PLUGIN_ID + "/debug/ResultCollector"));
	
	
	@Override
	public void start(BundleContext context) throws Exception {
		pluginInstance = this;
		super.start(context);
		initPlugin();
	}
	
	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		DeeProjectModel.dispose();
		pluginInstance = null;
	}
	
	
	public void initPlugin() throws CoreException {
		//TypeHierarchy.DEBUG = true;
		
		DeeProjectModel.initializeModel();
		DubProjectModel.initializeDefault();
	}
	
	/* *********************************************** */
	
}