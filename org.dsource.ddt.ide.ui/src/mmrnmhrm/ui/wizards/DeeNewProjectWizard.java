package mmrnmhrm.ui.wizards;


import mmrnmhrm.core.model.DeeNature;
import mmrnmhrm.ui.DeePlugin;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.wizards.GenericDLTKProjectWizard;
import org.eclipse.dltk.ui.wizards.ProjectWizard;
import org.eclipse.dltk.ui.wizards.ProjectWizardFirstPage;
import org.eclipse.dltk.ui.wizards.ProjectWizardSecondPage;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

/**
 * D New Project Wizard.
 * See also {@link GenericDLTKProjectWizard}
 */
public class DeeNewProjectWizard extends ProjectWizard implements IExecutableExtension {
	
	public static final String WIZARD_ID = DeePlugin.EXTENSIONS_IDPREFIX+"wizards.deeProjectWizard";
	
	protected ProjectWizardFirstPage fFirstPage;
	protected ProjectWizardSecondPage fSecondPage;
	protected DeeProjectWizardPage3 fThirdPage;
	
	private IConfigurationElement fConfigElement;
	
	public DeeNewProjectWizard() {
		super();
		//setDefaultPageImageDescriptor(RubyImages.DESC_WIZBAN_PROJECT_CREATION);
		setDialogSettings(DLTKUIPlugin.getDefault().getDialogSettings());
		setWindowTitle(DeeNewWizardMessages.LangNewProject_wizardTitle);
	}
	
	@Override
	public String getScriptNature() {
		return DeeNature.NATURE_ID;
	}
	
	@Override
	public void setInitializationData(IConfigurationElement cfig, String propertyName, Object data) {
		/* Stores the configuration element for the wizard. The config element will
		 * be used in <code>performFinish</code> to set the result perspective. */
		fConfigElement = cfig;
	}
	
	@Override
	public void addPages() {
		fFirstPage = new DeeProjectWizardPage1();
		fSecondPage = new DeeProjectWizardPage2(fFirstPage);
		fThirdPage = new DeeProjectWizardPage3(fSecondPage);
		addPage(fFirstPage);
		addPage(fSecondPage);
		addPage(fThirdPage);
	}
	
	@Override
	public boolean performFinish() {
		boolean res = super.performFinish();
		if (res) {
			BasicNewProjectResourceWizard.updatePerspective(fConfigElement);
			selectAndReveal(fSecondPage.getScriptProject().getProject());
		}
		return res;
	}
	
}
