/*******************************************************************************
 * Copyright (c) 2013, 2013 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.core.workspace;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.CoreUtil.array;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.Future;

import melnorme.lang.ide.core.LangCore;
import melnorme.lang.ide.core.bundlemodel.BundleModelManager;
import melnorme.lang.ide.core.utils.EclipseAsynchJobAdapter;
import melnorme.lang.ide.core.utils.EclipseAsynchJobAdapter.IRunnableWithJob;
import melnorme.lang.ide.core.utils.EclipseUtils;
import melnorme.lang.ide.core.utils.ResourceUtils;
import melnorme.lang.ide.core.utils.process.IRunProcessTask;
import melnorme.utilbox.concurrency.ITaskAgent;
import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.process.ExternalProcessHelper.ExternalProcessResult;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.DeeCoreMessages;
import mmrnmhrm.core.DeeCorePreferences;
import mmrnmhrm.core.engine_client.DubProcessManager;
import mmrnmhrm.core.engine_client.DubProcessManager.DubCompositeOperation;
import mmrnmhrm.core.workspace.DubModelManager.WorkspaceModelManagerTask;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ModelException;

import dtool.dub.BundlePath;
import dtool.dub.DubBundle;
import dtool.dub.DubBundle.DubBundleException;
import dtool.dub.DubBundleDescription;
import dtool.dub.DubHelper;
import dtool.dub.DubManifestParser;
import dtool.engine.compiler_installs.CompilerInstall;
import dtool.engine.compiler_installs.SearchCompilersOnPathOperation;

/**
 * Updates a {@link DubWorkspaceModel} when resource changes occur, using 'dub describe'.
 * Also creates problem markers on the Eclipse workspace. 
 */
public class DubModelManager extends BundleModelManager {
	
	
	public static final String DUB_PROBLEM_ID = DeeCore.PLUGIN_ID + ".DubProblem";
	
	protected final DubWorkspaceModel model;
	protected final DubProcessManager dubProcessManager = new DubProcessManager();
	
//	protected final SearchAndAddCompilersOnPathJob compilerSearchJob = new SearchAndAddCompilersOnPathJob();
	
	public DubModelManager(DubWorkspaceModel model) {
		this.model = model;
	}
	
	public DubProcessManager getProcessManager() {
		return dubProcessManager;
	}
	
//	public SearchAndAddCompilersOnPathJob getCompilersSearchJob() {
//		return compilerSearchJob;
//	}
	
	@Override
	protected void doShutdown() {
		super.doShutdown();
		dubProcessManager.shutdownNow();
	}
	
	@Override
	protected void initializeModelManager() {
//		// First of all, search for compilers on the path
//		compilerSearchJob.schedule();
//		try {
//			compilerSearchJob.join();
//		} catch (InterruptedException ie) {
//			// continue, we should still run rest of initialization
//		}
		
		super.initializeModelManager();
	}
	
	@Override
	protected void initializeProjectsInfo(IProgressMonitor monitor) {
		
		IProject[] projects = EclipseUtils.getWorkspaceRoot().getProjects();
		for (IProject project : projects) {
			if(isEligibleForBundleModelWatch(project) && projectHasBundleManifest(project)) {
				beginProjectDescribeUpdate(project);
			}
		}
		queueUpdateAllProjectsBuildpath(null);
	}
	
	@Override
	public DubBundleDescription getBundleInfo(IProject project) {
		return model.getBundleInfo(project);
	}
	
	protected static final Path DUB_BUNDLE_MANIFEST_FILE = new Path(BundlePath.DUB_MANIFEST_FILENAME);
	
	protected static boolean projectHasDubManifestFile(IProject project) {
		IResource packageFile = project.findMember(DUB_BUNDLE_MANIFEST_FILE);
		if(packageFile != null && packageFile.getType() == IResource.FILE) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean projectHasBundleManifest(IProject project) {
		return projectHasDubManifestFile(project);
	}
	
	@Override
	public boolean resourceDeltaIsBundleManifestChange(IResourceDelta resourceDelta) {
		return resourceDelta.getResource().getType() == IResource.FILE &&
				resourceDelta.getProjectRelativePath().equals(DUB_BUNDLE_MANIFEST_FILE);
	}
	
	@Override
	protected void bundleProjectAdded(IProject project) {
		beginProjectDescribeUpdate(project);
		queueUpdateAllProjectsBuildpath(project);
	}
	
	@Override
	protected void bundleProjectRemoved(IProject project) {
		removeProjectModel(project);
		queueUpdateAllProjectsBuildpath(project);
	}
	
	@Override
	protected void bundleManifestFileChanged(final IProject project) {
		beginProjectDescribeUpdate(project);
		// TODO: bug here, we should recalculate manifest for all files, not just buildpath
		queueUpdateAllProjectsBuildpath(project); // We do this because project might have changed name
	}
	
	protected Future<?> queueUpdateAllProjectsBuildpath(IProject project) {
		// TODO: this could be optimized to prevent duplicate tasks queued
		return modelAgent.submit(new UpdateAllProjectsBuildpathTask(this, project));
	}
	
	protected void beginProjectDescribeUpdate(final IProject project) {
		DubBundleDescription unresolvedDescription = readUnresolvedBundleDescription(project);
		ProjectInfo unresolvedProjectInfo = addProjectInfo(project, unresolvedDescription);
		
		modelAgent.submit(new ProjectModelDubDescribeTask(this, project, unresolvedProjectInfo));
	}
	
	protected DubBundleDescription readUnresolvedBundleDescription(final IProject project) {
		java.nio.file.Path location = project.getLocation().toFile().toPath();
		DubBundle unresolvedBundle = DubManifestParser.parseDubBundleFromLocation(BundlePath.create(location));
		
		return new DubBundleDescription(unresolvedBundle);
	}
	
	/* ----------------------------------- */
	
	protected class SearchCompilersOnPathOperation_Eclipse extends SearchCompilersOnPathOperation {
		@Override
		protected void handleWarning(String message) {
			DeeCore.logWarning(message);
		}
	}
	
	protected final ProjectInfo addProjectInfo(IProject project, DubBundleDescription dubBundleDescription) {
		CompilerInstall compilerInstall = new SearchCompilersOnPathOperation_Eclipse().
				searchForCompilersInDefaultPathEnvVars().getPreferredInstall();
		
		return model.addProjectInfo(project, dubBundleDescription, compilerInstall);
	}
	
	protected final void removeProjectModel(IProject project) {
		/*BUG here: updates to model should only occur in model agent. */
		model.removeProjectInfo(project);
	}
	
	public void syncPendingUpdates() {
		modelAgent.waitForPendingTasks();
	}
	
	/** WARNING: this API is intended to be used for tests only */
	public ITaskAgent internal_getModelAgent() {
		return modelAgent;
	}
	
	public static IMarker[] getDubErrorMarkers(IProject project) throws CoreException {
		return project.findMarkers(DUB_PROBLEM_ID, true, IResource.DEPTH_ONE);
	}
	
	protected abstract class WorkspaceModelManagerTask implements Runnable {
		
		protected final DubModelManager workspaceModelManager;
		
		public WorkspaceModelManagerTask() {
			this.workspaceModelManager = DubModelManager.this;
		}
		
		protected DubWorkspaceModel getModel() {
			return model;
		}
		
		protected void logInternalError(CoreException ce) {
			DeeCore.logInternalError(ce);
		}
		
	}
	
}


class ProjectModelDubDescribeTask extends ProjectUpdateBuildpathTask implements IRunnableWithJob {
	
	protected final IProject project;
	protected final ProjectInfo unresolvedProjectInfo;
	protected final  DubBundleDescription unresolvedDescription;
	
	protected ProjectModelDubDescribeTask(DubModelManager dubModelManager, IProject project, 
			ProjectInfo unresolvedProjectInfo) {
		super(dubModelManager);
		this.project = project;
		this.unresolvedProjectInfo = unresolvedProjectInfo;
		unresolvedDescription = unresolvedProjectInfo.getBundleDesc();
	}
	
	protected DubProcessManager getProcessManager() {
		return workspaceModelManager.dubProcessManager;
	}
	
	@Override
	public void run() {
		try {
			ResourceUtils.getWorkspace().run(new IWorkspaceRunnable() {
				
				@Override
				public void run(IProgressMonitor monitor) throws CoreException {
					if(project.exists() == false) {
						return;
					}
					deleteDubMarkers(project);
					
					if(unresolvedDescription.hasErrors() != false) {
						DubBundleException error = unresolvedDescription.getError();
						setDubErrorMarker(project, error);
						return; // only run dub describe if unresolved description had no errors
					}
					
				}
			}, project, 0, null);
		} catch (CoreException ce) {
			logInternalError(ce);
		}
		
		// only run dub describe if unresolved description had no errors
		if(unresolvedDescription.hasErrors() == false) {
			try {
				EclipseAsynchJobAdapter.runUnderAsynchJob(getNameForJob(), this);
			} catch (InterruptedException e) {
				return;
			}
		}
	}
	
	protected String getNameForJob() {
		return "Running 'dub describe' on project: " + project.getName();
	}
	
	protected void deleteDubMarkers(IProject project) throws CoreException {
		IMarker[] markers = DubModelManager.getDubErrorMarkers(project);
		for (IMarker marker : markers) {
			marker.delete();
		}
	}
	
	protected void setDubErrorMarker(IProject project, DubBundleException error) throws CoreException {
		setDubErrorMarker(project, error.getExtendedMessage());
	}
	
	protected void setDubErrorMarker(IProject project, String message) throws CoreException {
		IMarker dubMarker = project.createMarker(DubModelManager.DUB_PROBLEM_ID);
		dubMarker.setAttribute(IMarker.MESSAGE, message);
		dubMarker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
	}
	
	@Override
	public void runUnderEclipseJob(IProgressMonitor monitor) {
		assertNotNull(monitor);
		try {
			resolveProjectOperation(monitor);
		} catch (final CoreException ce) {
			try {
				EclipseUtils.getWorkspace().run(new IWorkspaceRunnable() {
					@Override
					public void run(IProgressMonitor monitor) throws CoreException {
						if(project.exists() == false) {
							return;
						}
						setProjectDubError(project, ce);
					}
				}, null, 0, monitor);
				
			} catch (CoreException e) {
				logInternalError(ce);
			}
		}
	}
	
	protected Void resolveProjectOperation(IProgressMonitor pm) throws CoreException {
		IPath projectLocation = project.getLocation();
		if(projectLocation == null) {
			return null; // Project no longer exists, or not stored in the local filesystem.
		}
		
		BundlePath bundlePath = BundlePath.create(projectLocation.toFile().toPath());
		
		String dubPath = DubHelper.getDubPath(DeeCorePreferences.getEffectiveDubPath());
		
		DubCompositeOperation resolveProjectOperation = new DubCompositeOperation(
			MessageFormat.format(DeeCoreMessages.RunningDubDescribe, project.getName()), project);
		getProcessManager().notifyOperationStarted(resolveProjectOperation);

//		TODO: when new DUB .22 is released
//		getProcessManager().submitDubCommandAndWait(resolveProjectOperation.newDubProcessTask(
//			project, array(dubPath, "upgrade", "--missing-only"), pm));
		
		IRunProcessTask dubDescribeTask = resolveProjectOperation.newDubProcessTask(
			project, array(dubPath, "describe"), pm);
		ExternalProcessResult processHelper = getProcessManager().submitDubCommandAndWait(dubDescribeTask);
		
		final DubBundleDescription bundleDesc = DubHelper.parseDubDescribe(bundlePath, processHelper);
		if(bundleDesc.hasErrors()) {
			throw LangCore.createCoreException("Error resolving bundle: ", bundleDesc.getError());
		}
		
		EclipseUtils.getWorkspace().run(new IWorkspaceRunnable() {
			@Override
			public void run(IProgressMonitor monitor) throws CoreException {
				if(project.exists() == false) {
					return;
				}
				assertTrue(!bundleDesc.hasErrors());
				
				workspaceModelManager.addProjectInfo(project, bundleDesc);
				updateBuildpath(project, bundleDesc);
			}
		}, null, 0, pm);
		
		return null;
	}
	
	protected void setProjectDubError(IProject project, CoreException ce) throws CoreException {
		
		DubBundleException dubError = new DubBundleException(ce.getMessage(), ce.getCause());
		
		DubBundle main = unresolvedDescription.getMainBundle();
		DubBundleDescription bundleDesc = new DubBundleDescription(main, dubError);
		workspaceModelManager.model.addProjectInfo(project, bundleDesc, unresolvedProjectInfo.compilerInstall);
		
		setDubErrorMarker(project, dubError);
	}
	
}

abstract class ProjectUpdateBuildpathTask extends WorkspaceModelManagerTask {
	
	protected ProjectUpdateBuildpathTask(DubModelManager dubModelManager) {
		dubModelManager.super();
	}
	
	protected void updateBuildpath(IProject project, DubBundleDescription bundleDesc) {
		IScriptProject projectElement = DLTKCore.create(project);
		
		ArrayList<IBuildpathEntry> entries = new ArrayList<>();
		
		for (java.nio.file.Path srcFolder : bundleDesc.getMainBundle().getEffectiveSourceFolders()) {
			IPath path2 = projectElement.getPath().append(srcFolder.toString());
			entries.add(DLTKCore.newSourceEntry(path2));
		}
		
		try {
			projectElement.setRawBuildpath(ArrayUtil.createFrom(entries, IBuildpathEntry.class), null);
		} catch (ModelException me) {
			logInternalError(me);
		}
	}
	
}

class UpdateAllProjectsBuildpathTask extends ProjectUpdateBuildpathTask {
	
	protected IProject changedProject;

	protected UpdateAllProjectsBuildpathTask(DubModelManager modelManager, IProject changedProject) {
		super(modelManager);
		this.changedProject = changedProject;
	}
	
	@Override
	public void run() {
		Set<String> dubProjects = getModel().getDubProjects();
		for (String projectName : dubProjects) {
			if(changedProject != null && changedProject.getName().equals(projectName))
				continue; // changedProject is supposed to be up to date, so no need to update that one.
			
			final IProject project = EclipseUtils.getWorkspaceRoot().getProject(projectName);
			final ProjectInfo projectInfo = getModel().getProjectInfo(project);
			
			// Check if project info exists, the project, might have been removed in the meanwhile.
			if(projectInfo == null) {
				continue;
			}
			
			try {
				// TODO: review this code for concurrency problems: 
				// EclipseUtils.getWorkspace().run was added!
				EclipseUtils.getWorkspace().run(new IWorkspaceRunnable() {
					
					@Override
					public void run(IProgressMonitor monitor) throws CoreException {
						if(project.exists() == false) {
							return;
						}
						updateBuildpath(project, projectInfo.getBundleDesc());
						
					}
				}, null);
			} catch (CoreException ce) {
				DeeCore.logStatus(ce);
			}
			
		}
	}
	
}