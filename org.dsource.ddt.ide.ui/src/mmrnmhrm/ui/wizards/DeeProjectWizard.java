package mmrnmhrm.ui.wizards;


import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;

import melnorme.lang.ide.core.utils.EclipseUtils;
import melnorme.lang.ide.ui.utils.WorkbenchUtils;
import melnorme.utilbox.misc.StringUtil;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.ui.DeePlugin;

import org.dsource.ddt.ide.core.DeeNature;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.wizards.GenericDLTKProjectWizard;
import org.eclipse.dltk.ui.wizards.ILocationGroup;
import org.eclipse.dltk.ui.wizards.ProjectWizardFirstPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.part.FileEditorInput;

import dtool.dub.DubManifestParser;

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
	protected void configureProjectBuildpath(IProject project, IProgressMonitor monitor) throws CoreException {
		IFile dubManifest = project.getFile(DubManifestParser.DUB_MANIFEST_FILENAME);
		if(!dubManifest.exists()) {
			
			final IFolder folder = project.getFolder("source");
			EclipseUtils.createFolder(folder, true, true, null);
			
			String dubManifestSource = getDefaultDubJSon();
			EclipseUtils.writeToFile(dubManifest, createInputStream(dubManifestSource, StringUtil.UTF8));
		}
	}
	
	protected ByteArrayInputStream createInputStream(String string, Charset charset) {
		return new ByteArrayInputStream(string.getBytes(charset));
	}
	
	protected String getDefaultDubJSon() {
		return 
			"{\n" +
				jsEntry("name", getProject().getName()) +",\n"+
				jsEntry("description", "A minimal D bundle.") +",\n"+
				jsEntryValue("dependencies", "{\n\t}") +"\n"+
			"}";
	}
	
	protected String jsEntry(String idString, String valueString) {
		return "\t" + '"' +idString+ '"' + " : " + '"' +valueString+ '"';
	}
	
	protected String jsEntryValue(String idString, String valueString) {
		return "\t" + '"' +idString+ '"' + " : " + valueString;
	}
	
	@Override
	protected void finishPage(IProgressMonitor monitor)
			throws InterruptedException, CoreException {
		getProjectCreator().performFinish(monitor);
	}
	
	@Override
	public boolean performFinish() {
		boolean res = super.performFinish();
		if (res) {
			final IProject project = getCreatedElement().getProject();
			IFile file = project.getFile(DubManifestParser.DUB_MANIFEST_FILENAME);
			
			IWorkbenchPage activePage = WorkbenchUtils.getActivePage();
			if(activePage != null) {
				try {
					activePage.openEditor(new FileEditorInput(file), EditorsUI.DEFAULT_TEXT_EDITOR_ID);
				} catch (PartInitException e) {
					DeeCore.log(e);
				}
			}
			return fBuildSettingsPage.performOk();
		}
		return res;
	}
	
}