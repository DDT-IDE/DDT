package mmrnmhrm.ui;

import melnorme.lang.ide.ui.LangUIPlugin;
import mmrnmhrm.core.projectmodel.DubModelManager;
import mmrnmhrm.ui.launch.DubCommandsConsoleListener;
import mmrnmhrm.ui.text.DeeTextTools;

import org.osgi.framework.BundleContext;


public class DeeUIPlugin extends LangUIPlugin {
	
	public static final String PLUGIN_ID = "org.dsource.ddt.ide.ui";
	
	public static DeeUIPlugin getDefault() {
		return (DeeUIPlugin) getInstance();
	}
	
	/* -------- start/stop methods -------- */
	
	private DubCommandsConsoleListener dubProcessListener;
	
	@Override
	protected void doCustomStart_initialStage(BundleContext context) {
		super.doCustomStart_initialStage(context);
	}
	
	@Override
	protected void doCustomStart_finalStage() {
		// Add process listener and start model manager. 
		dubProcessListener = new DubCommandsConsoleListener();
		DubModelManager.getDefault().getProcessManager().addDubProcessListener(dubProcessListener);
		DubModelManager.startDefault();
		
		super.doCustomStart_finalStage();
	}
	
	@Override
	protected void doCustomStop(BundleContext context) {
		DubModelManager.getDefault().getProcessManager().removeDubProcessListener(dubProcessListener);
	}
	
	/* --------  -------- */
	
	private DeeTextTools fTextTools;
	
	public DeeTextTools getTextTools() {
		if (fTextTools == null) {
			fTextTools = new DeeTextTools(true);
		}
		return fTextTools;
	}
	
}