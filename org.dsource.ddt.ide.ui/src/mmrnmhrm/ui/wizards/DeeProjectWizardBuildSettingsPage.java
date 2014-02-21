package mmrnmhrm.ui.wizards;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import mmrnmhrm.core.projectmodel.DeeProjectOptions;
import mmrnmhrm.ui.preferences.DeeProjectOptionsBlock;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.dltk.ui.wizards.IProjectWizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

public class DeeProjectWizardBuildSettingsPage extends WizardPage {
	
	private static final String PAGE_NAME = DeeProjectWizardBuildSettingsPage.class.getSimpleName();
	
	protected final DeeProjectWizard deeNewProjectWizard;
	protected final DeeProjectOptionsBlock fProjCfg;
	
	public DeeProjectWizardBuildSettingsPage(DeeProjectWizard deeNewProjectWizard) {
		super(PAGE_NAME);
		this.deeNewProjectWizard = deeNewProjectWizard;
		setTitle("Setup");
		setDescription("");
		
		fProjCfg = new DeeProjectOptionsBlock();
	}
	
	
	@Override
	public void createControl(Composite parent) {
		setControl(fProjCfg.createControl(parent));
	}
	
	@Override
	public void setVisible(boolean visible) {
		if(visible) {
			deeNewProjectWizard.pageChanged(this);
		}
		
		if (visible) {
			fProjCfg.init2(deeNewProjectWizard.getCreatedElement());
		} 
		super.setVisible(visible);
	}
	
	@Override
	public IProjectWizard getWizard() {
		return deeNewProjectWizard;
	}
	
	private IProject getProject() {
		return deeNewProjectWizard.getCreatedElement().getProject();
	}
	
	
	public boolean performOk() {
		return fProjCfg.performOk();
	}
	
	public void performCancel() {
		IFile file = getProject().getFile(DeeProjectOptions.CFG_FILE_NAME);
		if(file.exists())
			throw assertFail();
	}
	
}