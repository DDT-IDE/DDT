package org.dsource.ddt.ui;

import melnorme.lang.ide.ui.LangUIPlugin;
import melnorme.util.swt.SWTUtil;
import mmrnmhrm.core.build.DeeProjectBuilder;
import mmrnmhrm.core.projectmodel.DubModelManager;
import mmrnmhrm.ui.DeePluginImages;
import mmrnmhrm.ui.launch.DeeBuilderUIListener;
import mmrnmhrm.ui.launch.DubProcessUIListener;
import mmrnmhrm.ui.text.DeeTextTools;

import org.eclipse.core.runtime.Platform;
import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.osgi.framework.BundleContext;

import dtool.Logg;


public class DeeUIPlugin extends LangUIPlugin {
	
	public static final String PLUGIN_ID = "org.dsource.ddt.ide.ui";
	// The convention for the id prefix for extensions contributed by this plugin
	public static final String EXTENSIONS_IDPREFIX = PLUGIN_ID + ".";
	
	protected static DeeUIPlugin pluginInstance;
	
	/** Returns the plugin instance. */
	public static DeeUIPlugin getInstance() {
		return getDefault();
	}
	
	/** Returns the plugin instance. */
	public static DeeUIPlugin getDefault() {
		return pluginInstance;
	}
	
	private DeeBuilderUIListener listener;
	
	/* -------- start/stop methods -------- */
	
	@Override
	public void start(BundleContext context) throws Exception {
		pluginInstance = this;
		
		Logg.main.println(" =============  DDT INITIALIZING  ============= " );
		Logg.main.println("Location: " + Platform.getLocation());
		Logg.main.println("Instance Location: " + Platform.getInstanceLocation().getURL());
		
		super.start(context);
	}
	
	@Override
	protected Class<?> start_getImagesClass() {
		return DeePluginImages.class;
	}
	
	private DubProcessUIListener dubProcessListener;
	
	@Override
	protected void doCustomStart(BundleContext context) {
		SWTUtil.enableDebugColorHelpers = Platform.inDebugMode();
		
		listener = new DeeBuilderUIListener();
		DeeProjectBuilder.addDataListener(listener);
		
		dubProcessListener = new DubProcessUIListener();
		DubModelManager.getDefault().addDubProcessListener(dubProcessListener);
	}
	
	@Override
	public void stop(BundleContext context) throws Exception {
		DubModelManager.getDefault().removeDubProcessListener(dubProcessListener);
		
		DeeProjectBuilder.removeDataListener(listener);
		
		super.stop(context);
		pluginInstance = null;
	}
	
	/* --------  -------- */
	
	private DeeTextTools fTextTools;
	
	public ScriptTextTools getTextTools() {
		if (fTextTools == null) {
			fTextTools = new DeeTextTools(true);
		}
		return fTextTools;
	}
	
}