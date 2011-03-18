package mmrnmhrm.ui.wizards;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import mmrnmhrm.core.projectmodel.DeeProjectOptions;
import mmrnmhrm.ui.preferences.DeeProjectOptionsBlock;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.ui.wizards.ProjectWizardSecondPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

public class DeeProjectWizardPage3 extends WizardPage {
	
	private static final String PAGE_NAME = "DeeProjectWizardPage3";
	protected ProjectWizardSecondPage fSecondPage;
	protected DeeProjectOptionsBlock fProjCfg;
	
	public DeeProjectWizardPage3(ProjectWizardSecondPage secondPage) {
		super(PAGE_NAME);
		setTitle("Setup");
		setDescription("");
		
		fSecondPage = secondPage;
		fProjCfg = new DeeProjectOptionsBlock();
	}
	
	
	@Override
	public void createControl(Composite parent) {
		setControl(fProjCfg.createControl(parent));
	}
	
	@Override
	public void setVisible(boolean visible) {
		if (visible) {
			fProjCfg.init2(DLTKCore.create(getProject()));
		} 
		super.setVisible(visible);
	}
	
	
	private IProject getProject() {
		return fSecondPage.getScriptProject().getProject();
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