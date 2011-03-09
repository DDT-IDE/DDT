package mmrnmhrm.ui;

import melnorme.swtutil.SWTUtilExt;
import mmrnmhrm.core.build.DeeProjectBuilder;
import mmrnmhrm.lang.ui.InitializeAfterLoadJob;
import mmrnmhrm.lang.ui.LangPlugin;
import mmrnmhrm.ui.launch.DeeBuilderUIListener;
import mmrnmhrm.ui.text.DeeTextTools;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.osgi.framework.BundleContext;

import dtool.Logg;


public class DeePlugin extends LangPlugin {
	
	public static final String PLUGIN_ID = "org.dsource.ddt.ide.ui";
	// The convention for the id prefix for extensions contributed by this plugin
	public static final String EXTENSIONS_IDPREFIX = PLUGIN_ID + "."; 
	
	public static boolean initialized; 
	protected static DeePlugin pluginInstance;
	
	/** Returns the plugin instance. */
	public static DeePlugin getInstance() {
		return getDefault();
	}
	
	/** Returns the plugin instance. */
	public static DeePlugin getDefault() {
		return pluginInstance;
	}
	
	private DeeTextTools fTextTools;
	private DeeBuilderUIListener listener;
	
	
	@Override
	public void start(BundleContext context) throws Exception {
		pluginInstance = this;
		super.start(context);
		initPlugin();
		
		listener = new DeeBuilderUIListener();
		DeeProjectBuilder.addDataListener(listener);
		
		(new InitializeAfterLoadJob()).schedule();
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
		
		SWTUtilExt.enableDebugColorHelpers = Platform.inDebugMode();
	}
	
	public static void initializeAfterLoad(IProgressMonitor monitor) throws CoreException {
		// nothing to do
		monitor.done();
	}
	
	public ScriptTextTools getTextTools() {
		if (fTextTools == null) {
			fTextTools = new DeeTextTools(true);
		}
		return fTextTools;
	}
	
}