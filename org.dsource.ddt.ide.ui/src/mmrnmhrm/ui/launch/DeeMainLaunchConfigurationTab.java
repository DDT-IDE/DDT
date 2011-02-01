package mmrnmhrm.ui.launch;

import mmrnmhrm.core.model.DeeNature;
import mmrnmhrm.ui.DeePlugin;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.dltk.core.PreferencesLookupDelegate;
import org.eclipse.dltk.debug.core.DLTKDebugPreferenceConstants;
import org.eclipse.dltk.debug.ui.launchConfigurations.MainLaunchConfigurationTab;
import org.eclipse.swt.widgets.Composite;


public class DeeMainLaunchConfigurationTab extends MainLaunchConfigurationTab {

	public DeeMainLaunchConfigurationTab(String mode) {
		super(mode);
	}
	
	@Override
	public String getNatureID() {
		return DeeNature.NATURE_ID;
	}
	
	@Override
	protected boolean breakOnFirstLinePrefEnabled(PreferencesLookupDelegate delegate) {
		return delegate.getBoolean(DeePlugin.PLUGIN_ID,
				DLTKDebugPreferenceConstants.PREF_DBGP_BREAK_ON_FIRST_LINE);
	}

	@Override
	protected boolean dbpgLoggingPrefEnabled(PreferencesLookupDelegate delegate) {
		return delegate.getBoolean(DeePlugin.PLUGIN_ID,
				DLTKDebugPreferenceConstants.PREF_DBGP_ENABLE_LOGGING);
	}

	
	@Override
	protected void createMainModuleEditor(Composite parent, String text) {
		super.createMainModuleEditor(parent, text);
	}
	
	// Don't do any custom GUI controls for now
	@Override
	protected void updateProjectFromConfig(ILaunchConfiguration config) {
		super.updateProjectFromConfig(config);
		
		/*		
		IScriptProject deeProj;
		try {
			deeProj = DeeLaunchConfigurationDelegate.getScriptProject(config);
		} catch (CoreException e) {
			throw ExceptionAdapter.uncheckedTODO(e);
		}
		if(deeProj != null) {
			DeeProjectOptions deeProjectInfo = DeeModel.getDeeProjectInfo(deeProj);
			setProjectName(deeProjectInfo.getArtifactRelPath());
		}
	*/
	}
	
	@Override
	protected void doInitializeForm(ILaunchConfiguration config) {
		super.doInitializeForm(config);
	}
	
	@Override
	protected void updateMainModuleFromConfig(ILaunchConfiguration config) {
		super.updateMainModuleFromConfig(config);
	}
	

}
