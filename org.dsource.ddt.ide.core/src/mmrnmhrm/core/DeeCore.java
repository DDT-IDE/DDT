package mmrnmhrm.core;

import mmrnmhrm.core.dltk.DLTKModuleResolver;
import mmrnmhrm.core.model.DeeModel;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.BundleContext;

import dtool.refmodel.ReferenceResolver;

/**
 * Singleton class for D IDE Core.
 */
public class DeeCore extends LangCore {
	
	public static final String PLUGIN_ID = "org.dsource.ddt.ide.core";
	// The convention for the id prefix for extensions contributed by this plugin
	public static final String EXTENSIONS_IDPREFIX = PLUGIN_ID + "."; 

	protected static DeeCore pluginInstance;
	
	public DeeCore() {
		pluginInstance = this;
	}
	
	/** Returns the shared instance. */
	public static DeeCore getInstance() {
		return pluginInstance;
	}
	
	public static final boolean DEBUG_MODE = "true".equalsIgnoreCase(Platform.getDebugOption(PLUGIN_ID + "/debug/ResultCollector"));
	
	
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		initPlugin();
	}
	
	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		pluginInstance = null;
		DeeModel.dispose();
	}
	
	
	public void initPlugin() throws CoreException {
		//EntityResolver.initializeEntityResolver(DeeModel.getRoot());
		ReferenceResolver.initializeEntityResolver(DLTKModuleResolver.instance);
		//TypeHierarchy.DEBUG = true;
		
		DeeModel.initModel(); // Can we add a listener here?
	}
	
	/* *********************************************** */
	
}
