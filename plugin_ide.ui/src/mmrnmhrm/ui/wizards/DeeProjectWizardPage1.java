package mmrnmhrm.ui.wizards;

import org.eclipse.dltk.core.environment.EnvironmentManager;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.ui.wizards.ProjectWizardFirstPage;
import org.eclipse.swt.widgets.Composite;

public class DeeProjectWizardPage1 extends ProjectWizardFirstPage {
	
	protected final DeeProjectWizard deeNewProjectWizard;

	public DeeProjectWizardPage1(DeeProjectWizard deeNewProjectWizard) {
		super();
		setTitle(DeeNewWizardMessages.LangNewProject_Page1_pageTitle);
		setDescription(DeeNewWizardMessages.LangNewProject_Page1_pageDescription);
		this.deeNewProjectWizard = deeNewProjectWizard;
	}
	
	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
	}
	
	@Override
	protected LocationGroup createLocationGroup() {
		return new LocationGroup() {
			
			@Override
			public void createControls(Composite composite) {
				super.createControls(composite);
			}
			
			@Override
			protected void createModeControls(Composite group, int numColumns) {
				super.createModeControls(group, numColumns);
				fWorkspaceRadio.setLabelText(DeeNewWizardMessages.DltkNewProject_Page1_Location_workspaceDesc);
			}
			
			@Override
			protected void createEnvironmentControls(Composite group, int numColumns) {
				//super.createEnvironmentControls(group, numColumns);
				// Do nothing, prevent Environment control to be created
			}
			
			@Override
			protected boolean canChangeEnvironment() {
				return false;
			}
			
			@Override
			public IEnvironment getEnvironment() {
				return EnvironmentManager.getLocalEnvironment();
			}
		};
	}
	
	@Override
	protected boolean supportInterpreter() {
		return false;
	}
	
	@Override
	protected IInterpreterGroup createInterpreterGroup(Composite parent) {
		return null;
	}
	
	@Override
	protected boolean interpeterRequired() {
		return false;
	}
	
	@Override
	public boolean isSrc() {
		return true; // Create src+bin buildpath
	}
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if(visible) {
			deeNewProjectWizard.pageChanged(this);
		}
	}
	
}