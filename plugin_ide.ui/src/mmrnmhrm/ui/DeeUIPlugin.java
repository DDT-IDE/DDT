package mmrnmhrm.ui;

import org.osgi.framework.BundleContext;

import melnorme.lang.ide.core.ILangOperationsListener;
import melnorme.lang.ide.ui.LangUIPlugin;
import mmrnmhrm.ui.launch.DubCommandsConsoleListener;


public class DeeUIPlugin extends LangUIPlugin {
	
	public static final String PLUGIN_ID = "org.dsource.ddt.ide.ui";
	
	public static DeeUIPlugin getDefault() {
		return (DeeUIPlugin) getInstance();
	}
	
	public DeeUIPlugin() {
	}
	
	/* -------- start/stop methods -------- */
	
	
	@Override
	protected void doCustomStart_initialStage(BundleContext context) {
		super.doCustomStart_initialStage(context);
	}
	
	@Override
	protected ILangOperationsListener createOperationsConsoleListener() {
		return new DubCommandsConsoleListener();
	}
	
	@Override
	protected void doCustomStop(BundleContext context) {
	}
	
}