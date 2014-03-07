package mmrnmhrm.ui;

import melnorme.lang.ide.ui.LangUIPlugin;
import melnorme.util.swt.SWTUtil;
import mmrnmhrm.core.projectmodel.DubModelManager;
import mmrnmhrm.ui.launch.DubProcessUIListener;
import mmrnmhrm.ui.text.DeeTextTools;

import org.eclipse.core.runtime.Platform;
import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.osgi.framework.BundleContext;


public class DeeUIPlugin extends LangUIPlugin {
	
	public static final String PLUGIN_ID = "org.dsource.ddt.ide.ui";
	
	public static DeeUIPlugin getDefault() {
		return (DeeUIPlugin) getInstance();
	}
	
	/* -------- start/stop methods -------- */
	
	private DubProcessUIListener dubProcessListener;
	
	@Override
	protected Class<?> doCustomStart_getImagesClass() {
		return DeePluginImages.class;
	}
	
	@Override
	protected void doCustomStart(BundleContext context) {
		SWTUtil.enableDebugColorHelpers = Platform.inDebugMode();
		
		dubProcessListener = new DubProcessUIListener();
		DubModelManager.getDefault().getProcessManager().addDubProcessListener(dubProcessListener);
		DubModelManager.startDefault();
	}
	
	@Override
	protected void doCustomStop(BundleContext context) {
		DubModelManager.getDefault().getProcessManager().removeDubProcessListener(dubProcessListener);
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