package org.dsource.ddt.ui;

import melnorme.lang.ide.ui.LangPlugin;
import melnorme.util.swt.SWTUtil;
import melnorme.utilbox.misc.MiscUtil;
import mmrnmhrm.core.build.DeeProjectBuilder;
import mmrnmhrm.ui.DeePluginImages;
import mmrnmhrm.ui.launch.DeeBuilderUIListener;
import mmrnmhrm.ui.text.DeeTextTools;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

import dtool.Logg;


public class DeeUIPlugin extends LangPlugin {
	
	public static final String PLUGIN_ID = "org.dsource.ddt.ide.ui";
	// The convention for the id prefix for extensions contributed by this plugin
	public static final String EXTENSIONS_IDPREFIX = PLUGIN_ID + ".";
	
	// ID to start the debug plugin automatically, if present
	private static final String DEBUG_PLUGIN_ID = "org.dsource.ddt.ide.debug";
	
	protected static DeeUIPlugin pluginInstance;
	
	/** Returns the plugin instance. */
	public static DeeUIPlugin getInstance() {
		return getDefault();
	}
	
	/** Returns the plugin instance. */
	public static DeeUIPlugin getDefault() {
		return pluginInstance;
	}
	
	private DeeTextTools fTextTools;
	private DeeBuilderUIListener listener;
	
	
	@Override
	public void start(BundleContext context) throws Exception {
		pluginInstance = this;
		super.start(context);
		initPlugin();
		
		MiscUtil.loadClass(DeePluginImages.class); // Fail fast if resources not found
		
		listener = new DeeBuilderUIListener();
		DeeProjectBuilder.addDataListener(listener);
		
		startInitializeAfterLoadJob();
	}
	
	@Override
	public void stop(BundleContext context) throws Exception {
		DeeProjectBuilder.removeDataListener(listener);
		
		super.stop(context);
		pluginInstance = null;
	}
	
	public void initPlugin() throws CoreException {
		Logg.main.println(" =============  Mmrnmhrm INITIALIZING  ============= " );
		Logg.main.println("Location: " + Platform.getLocation());
		Logg.main.println("Instance Location: " + Platform.getInstanceLocation().getURL());
		
		SWTUtil.enableDebugColorHelpers = Platform.inDebugMode();
		
		startDebugPlugin();
	}
	
	private static void startDebugPlugin() {
		// Force start of debug plugin, if present, so that UI contributions will be fully active.
		// ATM, some UI contributions that dynamically manipulate enablement and state don't work correctly
		// unless underlying plugin is started.
		try {
			Bundle debugPlugin = Platform.getBundle(DEBUG_PLUGIN_ID);
			if(debugPlugin != null) {
				debugPlugin.start(Bundle.START_TRANSIENT);
			}
		} catch (BundleException e) {
			log(e);
		}
	}
	
	public ScriptTextTools getTextTools() {
		if (fTextTools == null) {
			fTextTools = new DeeTextTools(true);
		}
		return fTextTools;
	}
	
}