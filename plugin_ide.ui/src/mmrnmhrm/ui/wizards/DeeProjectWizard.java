/*******************************************************************************
 * Copyright (c) 2013 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui.wizards;


import static melnorme.utilbox.misc.MiscUtil.getClassResource;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.WizardPage;

import dtool.dub.BundlePath;
import melnorme.lang.ide.core.operations.ToolchainPreferences;
import melnorme.lang.ide.core.utils.prefs.PreferenceHelper;
import melnorme.lang.ide.ui.dialogs.LangNewProjectWizard;
import melnorme.lang.ide.ui.dialogs.LangProjectWizardFirstPage;
import melnorme.lang.ide.ui.dialogs.WizardMessages;
import melnorme.lang.tooling.data.ValidationException;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.build.DubLocationValidator;

/**
 * D New Project Wizard.
 */
public class DeeProjectWizard extends LangNewProjectWizard {
	
	protected static final String HelloWorld_DubJsonTemplate = getClassResource(
		DeeProjectWizard.class, "hello_world.dub.json");
	protected static final String HelloWorld_ModuleContents = getClassResource(
		DeeProjectWizard.class, "hello_world.d");
	
	
	protected final DeeProjectWizardFirstPage firstPage = new DeeProjectWizardFirstPage();
//	protected final DeeProjectWizardBuildSettingsPage buildSettingsPage = new DeeProjectWizardBuildSettingsPage(this);
	
	public DeeProjectWizard() {
	}
	
	@Override
	public LangProjectWizardFirstPage getFirstPage() {
		return firstPage;
	}
	
	@Override
	public WizardPage getSecondPage() {
		return null;
//		return buildSettingsPage;
	}
	
	@Override
	public void addPages() {
		addPage(firstPage);
//		addPage(buildSettingsPage);
	}
	
	@Override
	protected ProjectCreator_ForWizard createProjectCreator() {
		return new DeeProjectCreator();
	}
	
	public class DeeProjectCreator extends ProjectCreator_ForWizard {
		
		@Override
		public boolean revertProjectCreation() {
			syncPendingModelUpdates();
			return super.revertProjectCreation();
		}
		
		// Wait for pending operations, has they be locking the workspace, even if externally.
		// TODO: a cleaner way to solve this? Perhaps have the project locked by ModelManager?
		protected void syncPendingModelUpdates() {
			IRunnableWithProgress op = new IRunnableWithProgress() {
				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					DeeCore.getDeeBundleModelManager().syncPendingUpdates();
				}
			};
			try {
				getContainer().run(true, true, op);
			} catch (InvocationTargetException e) {
				DeeCore.logError("Error synching with ModelManager", e);
			} catch (InterruptedException e) {
			}
		}
		
	}
	
	@Override
	protected void configureCreatedProject(ProjectCreator_ForWizard projectCreator, IProgressMonitor pm)
			throws CoreException {
		String bundleName = getProject().getName().toLowerCase();
		String dubManifestContents = HelloWorld_DubJsonTemplate.replace("%BUNDLE_NAME%", bundleName);
		projectCreator.createFile(getProject().getFile(BundlePath.DUB_MANIFEST_FILENAME), 
			dubManifestContents, false, pm);
		
		IFile mainModule = getProject().getFolder("src").getFile("app.d");
		projectCreator.createFile(mainModule, HelloWorld_ModuleContents, true, pm);
	}
	
	@Override
	public boolean performFinish() {
		boolean res = super.performFinish();
//		if(res) {
//			buildSettingsPage.performOk();
//			return true;
//		}
		return res;
	}
	
}

class DeeProjectWizardFirstPage extends LangProjectWizardFirstPage {
	
	public DeeProjectWizardFirstPage() {
		setTitle(WizardMessages.LangNewProject_Page1_pageTitle);
		setDescription(WizardMessages.LangNewProject_Page1_pageDescription);
	}
	
	@Override
	protected void validatePreferences() throws ValidationException {
		PreferenceHelper<String> globalPref = ToolchainPreferences.SDK_PATH2.getGlobalPreference();
		new DubLocationValidator().getValidatedField(globalPref.get());
	}
	
}