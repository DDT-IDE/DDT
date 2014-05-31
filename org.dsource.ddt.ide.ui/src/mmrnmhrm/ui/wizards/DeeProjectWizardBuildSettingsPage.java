package mmrnmhrm.ui.wizards;

import mmrnmhrm.ui.preferences.DubProjectOptionsBlock;

import org.eclipse.dltk.ui.wizards.IProjectWizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

public class DeeProjectWizardBuildSettingsPage extends WizardPage {
	
	private static final String PAGE_NAME = DeeProjectWizardBuildSettingsPage.class.getSimpleName();
	
	protected final DeeProjectWizard deeNewProjectWizard;
	protected final DubProjectOptionsBlock prjBuildOptionsBlock = new DubProjectOptionsBlock();
	
	public DeeProjectWizardBuildSettingsPage(DeeProjectWizard deeNewProjectWizard) {
		super(PAGE_NAME);
		this.deeNewProjectWizard = deeNewProjectWizard;
		setTitle("Configure DUB build options");
	}
	
	
	@Override
	public void createControl(Composite parent) {
		setControl(prjBuildOptionsBlock.createComponent(parent));
	}
	
	@Override
	public void setVisible(boolean visible) {
		if(visible) {
			deeNewProjectWizard.pageChanged(this);
		}
		
		if (visible) {
			prjBuildOptionsBlock.initializeFrom(deeNewProjectWizard.getCreatedElement().getProject());
		}
		super.setVisible(visible);
	}
	
	@Override
	public IProjectWizard getWizard() {
		return deeNewProjectWizard;
	}
	
	public boolean performOk() {
		return prjBuildOptionsBlock.performOk();
	}
	
}