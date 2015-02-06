/*******************************************************************************
 * Copyright (c) 2013, 2015 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui.wizards;


import static melnorme.utilbox.misc.MiscUtil.getClassResourceAsString;

import java.lang.reflect.InvocationTargetException;

import melnorme.lang.ide.core.utils.ResourceUtils;
import melnorme.lang.ide.ui.dialogs.LangNewProjectWizard;
import melnorme.lang.ide.ui.dialogs.LangProjectWizardFirstPage;
import melnorme.lang.ide.ui.dialogs.WizardMessages;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.ui.DeeUIPlugin;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.WizardPage;

import dtool.dub.BundlePath;

/**
 * D New Project Wizard.
 */
public class DeeProjectWizard extends LangNewProjectWizard {
	
	public static final String WIZARD_ID = DeeUIPlugin.PLUGIN_ID + ".wizards.deeProjectWizard";
	
	protected static final String HelloWorld_DubJsonTemplate = getClassResourceAsString(
		DeeProjectWizard.class, "hello_world.dub.json");
	protected static final String HelloWorld_ModuleContents = getClassResourceAsString(
		DeeProjectWizard.class, "hello_world.d");
	
	
	protected final DeeProjectWizardFirstPage firstPage = new DeeProjectWizardFirstPage();
	protected final DeeProjectWizardBuildSettingsPage buildSettingsPage = new DeeProjectWizardBuildSettingsPage(this);
	
	public DeeProjectWizard() {
	}
	
	@Override
	public LangProjectWizardFirstPage getFirstPage() {
		return firstPage;
	}
	
	@Override
	public WizardPage getSecondPage() {
		return buildSettingsPage;
	}
	
	@Override
	public void addPages() {
		addPage(firstPage);
		addPage(buildSettingsPage);
	}
	
	@Override
	protected ProjectCreator_ForWizard createProjectCreator() {
		return new DeeProjectCreator();
	}
	
	public class DeeProjectCreator extends ProjectCreator_ForWizard {
		
		public DeeProjectCreator() {
			super(DeeProjectWizard.this);
		}
		
		@Override
		protected void configureCreatedProject(IProgressMonitor monitor) throws CoreException {
			IProject project = getProject();
			final IFile dubManifest = project.getFile(BundlePath.DUB_MANIFEST_FILENAME);
			if(!dubManifest.exists()) {
				
				final IFolder folder = project.getFolder("source");
				ResourceUtils.createFolder(folder, true, true, null);
				
				final IFile appFile = folder.getFile("app.d");
				ResourceUtils.writeStringToFile(appFile, HelloWorld_ModuleContents);
				
				ResourceUtils.writeStringToFile(dubManifest, getDefaultDubJSon());
				
				revertActions.add(new IRevertAction() {
					@Override
					public void run(IProgressMonitor monitor) throws CoreException {
						dubManifest.delete(false, monitor);
						appFile.delete(false, monitor);
						if(folder.members().length == 0) {
							folder.delete(false, monitor);
						}
					}
				});
			}
		}
		
		protected String getDefaultDubJSon() {
			return HelloWorld_DubJsonTemplate.replace("%BUNDLE_NAME%", getProject().getName());
		}
		
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
					DeeCore.getWorkspaceModelManager().syncPendingUpdates();
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
	public boolean performFinish() {
		boolean res = super.performFinish();
		if (res) {
			buildSettingsPage.performOk();
			
			IFile file = getProject().getFile(BundlePath.DUB_MANIFEST_FILENAME);
			openEditorOnFile(file);
			
			return true;
		}
		return res;
	}
	
}

class DeeProjectWizardFirstPage extends LangProjectWizardFirstPage {
	
	public DeeProjectWizardFirstPage() {
		setTitle(WizardMessages.LangNewProject_Page1_pageTitle);
		setDescription(WizardMessages.LangNewProject_Page1_pageDescription);
	}
	
}