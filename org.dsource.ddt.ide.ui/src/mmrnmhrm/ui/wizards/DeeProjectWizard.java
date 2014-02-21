package mmrnmhrm.ui.wizards;


import mmrnmhrm.ui.DeePlugin;

import org.dsource.ddt.ide.core.DeeNature;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.wizards.GenericDLTKProjectWizard;
import org.eclipse.dltk.ui.wizards.ILocationGroup;
import org.eclipse.dltk.ui.wizards.ProjectWizardFirstPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Display;

/**
 * D New Project Wizard.
 * See also {@link GenericDLTKProjectWizard}
 */
public class DeeProjectWizard extends ProjectWizardExtension {
	
	public static final String WIZARD_ID = DeePlugin.EXTENSIONS_IDPREFIX+"wizards.deeProjectWizard";
	
	protected final ProjectWizardFirstPage fFirstPage = new DeeProjectWizardPage1(this);
	protected final DeeProjectWizardBuildSettingsPage fBuildSettingsPage = 
			new DeeProjectWizardBuildSettingsPage(this);
	
	public DeeProjectWizard() {
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
	protected ILocationGroup getFirstPage() {
		return fFirstPage;
	}
	
	@Override
	public void setInitializationData(IConfigurationElement cfig, String propertyName, Object data) {
		super.setInitializationData(cfig, propertyName, data);
	}
	
	@Override
	public IScriptProject getCreatedElement() {
		return DLTKCore.create(getProject());
	}
	
	@Override
	public void addPages() {
		addPage(fFirstPage);
		addPage(fBuildSettingsPage);
	}
	
	public void pageChanged(final WizardPage newVisiblePage) {
		if (newVisiblePage instanceof DeeProjectWizardPage1) {
			removeProject();
		} else if(!getCreatedElement().exists()) {
			try {
				createProject();
			} catch (OperationCanceledException e) {
				Display.getCurrent().asyncExec(new Runnable() {
					@Override
					public void run() {
						getContainer().showPage(newVisiblePage.getPreviousPage());
					}
				});
			}
		}
	}
	
	@Override
	public boolean performFinish() {
		return super.performFinish();
	}
	
}