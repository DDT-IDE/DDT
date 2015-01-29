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
import melnorme.lang.ide.core.utils.CoreTaskAgent;
import melnorme.lang.ide.core.utils.DefaultProjectResourceListener;
import melnorme.lang.ide.core.utils.EclipseAsynchJobAdapter;
import melnorme.lang.ide.core.utils.EclipseAsynchJobAdapter.IRunnableWithJob;
import melnorme.lang.ide.core.utils.EclipseUtils;
import melnorme.lang.ide.core.utils.ResourceUtils;
import melnorme.lang.ide.core.utils.process.IRunProcessTask;
import melnorme.utilbox.concurrency.ITaskAgent;
import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.misc.Location;
import melnorme.utilbox.misc.SimpleLogger;
import melnorme.utilbox.process.ExternalProcessHelper.ExternalProcessResult;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.DeeCoreMessages;
import mmrnmhrm.core.DeeCorePreferences;
import mmrnmhrm.core.engine_client.DubProcessManager;
import mmrnmhrm.core.engine_client.DubProcessManager.DubCompositeOperation;
import mmrnmhrm.core.engine_client.SearchAndAddCompilersTask.SearchAndAddCompilersOnPathJob;
import mmrnmhrm.core.workspace.WorkspaceModelManager.WorkspaceModelManagerTask;

import org.dsource.ddt.ide.core.DeeNature;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.launching.ScriptRuntime;

import dtool.dub.BundlePath;
import dtool.dub.DubBundle;
import dtool.dub.DubBundle.DubBundleException;
import dtool.dub.DubBundleDescription;
import dtool.dub.DubHelper;
import dtool.dub.DubManifestParser;
import dtool.engine.compiler_installs.CompilerInstall;
import dtool.engine.compiler_installs.SearchCompilersOnPathOperation;

/**
 * Updates a {@link WorkspaceModel} when resource changes occur, using 'dub describe'.
 * Also creates problem markers on the Eclipse workspace. 
 */
public class WorkspaceModelManager {
	
	protected static SimpleLogger log = new SimpleLogger(Platform.inDebugMode());
	
	/* ----------------------------------- */
	
	public static final String DUB_PROBLEM_ID = DeeCore.PLUGIN_ID + ".DubProblem";
	
	protected final WorkspaceModel model;
	protected final DubProjectModelResourceListener listener = new DubProjectModelResourceListener();
	protected final ITaskAgent modelAgent = new CoreTaskAgent(getClass().getSimpleName());
	protected final DubProcessManager dubProcessManager = new DubProcessManager();
	
	protected final SearchAndAddCompilersOnPathJob compilerSearchJob = new SearchAndAddCompilersOnPathJob();
	
	protected boolean started = false;

	public WorkspaceModelManager(WorkspaceModel model) {
		this.model = model;
	}
	
	public DubProcessManager getProcessManager() {
		return dubProcessManager;
	}
	
	public SearchAndAddCompilersOnPathJob getCompilersSearchJob() {
		return compilerSearchJob;
	}
	
	public void startManager() {
		log.print("==> Starting: " + getClass().getSimpleName());
		assertTrue(started == false); // start only once
		started = true;
		
		// Run heavyweight initialization in executor thread.
		// This is necessary so that we avoid running the initialization during plugin initialization.
		// Otherwise there could be problems because initialization is heavyweight code:
		// it requests workspace locks (which may not be available) and issues workspace deltas
		modelAgent.submit(new Runnable() {
			@Override
			public void run() {
				initializeModelManager();
			}
		});
	}
	
	public void shutdownManager() {
		// It is possible to shutdown the manager without having it started.
		
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(listener);
		// shutdown model manager agent first, since model agent uses dub process agent
		modelAgent.shutdownNow();
		dubProcessManager.shutdownNow();
		try {
			modelAgent.awaitTermination();
		} catch (InterruptedException e) {
			DeeCore.logInternalError(e);
		}
	}
	
	protected void initializeModelManager() {
		// First of all, search for compilers on the path
		compilerSearchJob.schedule();
		try {
			compilerSearchJob.join();
		} catch (InterruptedException ie) {
			// continue, we should still run rest of initialization
		}
		
		try {
			EclipseUtils.getWorkspace().run(new IWorkspaceRunnable() {
				@Override
				public void run(IProgressMonitor monitor) {
					EclipseUtils.getWorkspace().addResourceChangeListener(listener, IResourceChangeEvent.POST_CHANGE);
					initializeProjectsInfo(monitor);
				}
			}, null);
		} catch (CoreException ce) {
			DeeCore.logStatus(ce);
			// This really should not happen, but still try to recover by registering listener.
			EclipseUtils.getWorkspace().addResourceChangeListener(listener, IResourceChangeEvent.POST_CHANGE);
		}
	}
	
	protected void initializeProjectsInfo(@SuppressWarnings("unused") IProgressMonitor monitor) {
		try {
			IProject[] deeProjects = EclipseUtils.getOpenedProjects(DeeNature.NATURE_ID);
			for (IProject project : deeProjects) {
				if(projectHasDubManifestFile(project)) {
					beginProjectDescribeUpdate(project);
				}
			}
			queueUpdateAllProjectsBuildpath(null);
			
		} catch (CoreException ce) {
			DeeCore.logStatus(ce.getStatus());
		}
	}
	
	protected static final Path DUB_BUNDLE_MANIFEST_FILE = new Path(BundlePath.DUB_MANIFEST_FILENAME);
	
	protected static boolean projectHasDubManifestFile(IProject project) {
		IResource packageFile = project.findMember(DUB_BUNDLE_MANIFEST_FILE);
		if(packageFile != null && packageFile.getType() == IResource.FILE) {
			return true;
		}
		return false;
	}
	
	protected final class DubProjectModelResourceListener extends DefaultProjectResourceListener {
		
		@Override
		protected void processProjectDelta(IResourceDelta projectDelta) {
			IProject project = (IProject) projectDelta.getResource();
			
			DubBundleDescription existingProjectModel = model.getBundleInfo(project);
			
			if(projectDelta.getKind() == IResourceDelta.REMOVED || !DeeNature.isAccessible(project, true)) {
				if(existingProjectModel == null) {
					return; // Nothing to remove, might not have been a DUB model project.
				}
				dubProjectRemoved(project);
				return;
			}
			
			switch(projectDelta.getKind()) {
			case IResourceDelta.ADDED:
				if(projectHasDubManifestFile(project)) {
					dubProjectAdded(project);
				}
				break;
			case IResourceDelta.CHANGED:
				if((projectDelta.getFlags() & IResourceDelta.DESCRIPTION) != 0) {
					// It might be the case that project wasn't a DUB model project before, and now is.
					if(existingProjectModel == null) {
						// Then it's true, project has become DUB model project.
						if(projectHasDubManifestFile(project)) {
							dubProjectAdded(project);
						}
						return;
					}
				}
				IResourceDelta[] resourceDeltas = projectDelta.getAffectedChildren();
				if(resourceDeltas == null)
					break;
				for (IResourceDelta resourceDelta : resourceDeltas) {
					if(resourceDelta.getResource().getType() == IResource.FILE && 
							resourceDelta.getProjectRelativePath().equals(DUB_BUNDLE_MANIFEST_FILE)) {
						if(resourceDelta.getResource().getType() == IResource.FILE) {
							dubManifestFileChanged(project);
						}
					}
				}
				break;
			}
		}
		
	}
	
	protected void dubProjectAdded(IProject project) {
		beginProjectDescribeUpdate(project);
		queueUpdateAllProjectsBuildpath(project);
	}
	
	protected void dubProjectRemoved(IProject project) {
		removeProjectModel(project);
		queueUpdateAllProjectsBuildpath(project);
	}
	
	protected void dubManifestFileChanged(final IProject project) {
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
		
		protected final WorkspaceModelManager workspaceModelManager;
		
		public WorkspaceModelManagerTask() {
			this.workspaceModelManager = WorkspaceModelManager.this;
		}
		
		protected WorkspaceModel getModel() {
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
	
	protected ProjectModelDubDescribeTask(WorkspaceModelManager dubModelManager, IProject project, 
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
		IMarker[] markers = WorkspaceModelManager.getDubErrorMarkers(project);
		for (IMarker marker : markers) {
			marker.delete();
		}
	}
	
	protected void setDubErrorMarker(IProject project, DubBundleException error) throws CoreException {
		setDubErrorMarker(project, error.getExtendedMessage());
	}
	
	protected void setDubErrorMarker(IProject project, String message) throws CoreException {
		IMarker dubMarker = project.createMarker(WorkspaceModelManager.DUB_PROBLEM_ID);
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
	
	protected ProjectUpdateBuildpathTask(WorkspaceModelManager dubModelManager) {
		dubModelManager.super();
	}
	
	protected void updateBuildpath(IProject project, DubBundleDescription bundleDesc) {
		IScriptProject projectElement = DLTKCore.create(project);
		
		ArrayList<IBuildpathEntry> entries = new ArrayList<>();
		
		entries.add(DLTKCore.newContainerEntry(ScriptRuntime.newDefaultInterpreterContainerPath()));
		entries.add(DLTKCore.newContainerEntry(new Path(DubDependenciesBuildpathContainer.CONTAINER_PATH_ID)));
		
		for (java.nio.file.Path srcFolder : bundleDesc.getMainBundle().getEffectiveSourceFolders()) {
			IPath path2 = projectElement.getPath().append(srcFolder.toString());
			entries.add(DLTKCore.newSourceEntry(path2));
		}
		
		try {
			IBuildpathEntry[] bpEntriesFromDeps = getBuildpathEntriesFromDeps(bundleDesc);
			updateDubBuildpathContainer(projectElement, bpEntriesFromDeps);
			projectElement.setRawBuildpath(ArrayUtil.createFrom(entries, IBuildpathEntry.class), null);
		} catch (ModelException me) {
			logInternalError(me);
		}
	}
	
	protected void updateDubBuildpathContainer(IScriptProject projectElement, IBuildpathEntry[] entries) 
			throws ModelException {
		DubDependenciesBuildpathContainer dubContainer = new DubDependenciesBuildpathContainer(projectElement, entries);
		DLTKCore.setBuildpathContainer(DubDependenciesBuildpathContainer.CONTAINER_PATH, array(projectElement), 
			array(dubContainer), null);
	}
	
	protected IBuildpathEntry[] getBuildpathEntriesFromDeps(DubBundleDescription bundleDesc) {
		ArrayList<IBuildpathEntry> depEntries = new ArrayList<>();
		for (DubBundle depBundle : bundleDesc.getBundleDependencies()) {
			BundlePath bundlePath = depBundle.getBundlePath();
			if(depBundle.hasErrors() || bundlePath == null) {
				continue;
			}
			IProject workspaceProject = findProjectForBundle(depBundle);
			if(workspaceProject != null) {
				depEntries.add(DLTKCore.newProjectEntry(workspaceProject.getFullPath(), true));
			} else {
				for (java.nio.file.Path srcFolder : depBundle.getEffectiveSourceFolders()) {
					
					Location srcFolderAbsolute = bundlePath.resolve(srcFolder);
					depEntries.add(
						DubDependenciesBuildpathContainer.createDubBuildpathEntry(
							EclipseUtils.epath(srcFolderAbsolute)));
				}
			}
		}
		
		return ArrayUtil.createFrom(depEntries, IBuildpathEntry.class);
	}
	
	protected IProject findProjectForBundle(DubBundle depBundle) {
		return findProjectForBundleLocation(depBundle.getLocationPath());
	}
	
	protected IProject findProjectForBundleLocation(java.nio.file.Path location) {
		if(location == null) {
			return null;
		}
		
		// BM: There is a minor race condition here that will cause a temporary inconsistent state.
		// TODO: to avoid that we should maintain a mapping of location->project in Dub Model.
		IProject[] deeProjects;
		try {
			deeProjects = EclipseUtils.getOpenedProjects(DeeCore.NATURE_ID);
		} catch (CoreException e) {
			return null;
		}
		for (IProject project : deeProjects) {
			if(project.getLocation().toFile().toPath().equals(location) && getModel().getBundleInfo(project) != null) {
				return project;
			}
		}
		
		return null;
	}
}

class UpdateAllProjectsBuildpathTask extends ProjectUpdateBuildpathTask {
	
	protected IProject changedProject;

	protected UpdateAllProjectsBuildpathTask(WorkspaceModelManager modelManager, IProject changedProject) {
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