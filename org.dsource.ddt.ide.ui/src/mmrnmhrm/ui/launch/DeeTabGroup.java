package mmrnmhrm.ui.launch;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.EnvironmentTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.dltk.debug.ui.launchConfigurations.ScriptCommonTab;

public class DeeTabGroup extends AbstractLaunchConfigurationTabGroup {
	
	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
				new DeeMainLaunchConfigurationTab(mode),
				new DeeScriptArgumentsTab(),
				//new RubyInterpreterTab(),
				new EnvironmentTab(),
				new ScriptCommonTab()
		};
		
		setTabs(tabs);
	}
	
}
