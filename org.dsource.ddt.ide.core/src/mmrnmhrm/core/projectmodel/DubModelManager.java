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
package mmrnmhrm.core.projectmodel;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.CoreUtil.array;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import melnorme.lang.ide.core.utils.EclipseAsynchJobAdapter;
import melnorme.lang.ide.core.utils.EclipseAsynchJobAdapter.IRunnableWithJob;
import melnorme.lang.ide.core.utils.EclipseUtils;
import melnorme.utilbox.concurrency.ExternalProcessOutputHelper;
import melnorme.utilbox.concurrency.ITaskAgent;
import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.misc.StringUtil;
import mmrnmhrm.core.CoreTaskAgent;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.DeeCorePreferences;
import mmrnmhrm.core.projectmodel.DubModelManager.DubModelManagerTask;
import mmrnmhrm.core.projectmodel.SearchAndAddCompilersOnPathTask.SearchAndAddCompilersOnPathJob;

import org.dsource.ddt.ide.core.DeeNature;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
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
import org.eclipse.dltk.launching.ScriptRuntime;

import dtool.SimpleLogger;
import dtool.dub.DubBundle;
import dtool.dub.DubBundle.DubBundleException;
import dtool.dub.DubBundleDescription;
import dtool.dub.DubDescribeParser;
import dtool.dub.DubManifestParser;

/**
 * Updates {@link DubModel} when resource changes occur, using 'dub describe' 
 */
public class DubModelManager {
	
	protected static SimpleLogger log = new SimpleLogger(true);
	
	protected static final DubModelManager defaultInstance = new DubModelManager(DubModel.getDefault());
	
	public static void startDefault() {
		defaultInstance.startManager();
	}
	
	public static void shutdownDefault() {
		defaultInstance.shutdownManager();
	}
	
	public static DubModelManager getDefault() {
		return defaultInstance;
	}
	
	/* ----------------------------------- */
	
	public static final String DUB_PROBLEM_ID = DeeCore.EXTENSIONS_IDPREFIX + "DubProblem";
	
	protected final DubModel model;
	protected final DubProjectModelResourceListener listener = new DubProjectModelResourceListener();
	protected boolean started = false;
	protected final ITaskAgent modelAgent = new CoreTaskAgent(getClass().getSimpleName());
	protected final DubProcessManager dubProcessManager = new DubProcessManager();
	
	public DubModelManager(DubModel model) {
		this.model = model;
	}
	
	public DubProcessManager getProcessManager() {
		return dubProcessManager;
	}
	
	public void startManager() {
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
		
		DeeCore.getWorkspace().removeResourceChangeListener(listener);
		// shutdown model manager agent first, since model agent uses dub process agent
		modelAgent.shutdownNow();
		dubProcessManager.shutdownNow();
		try {
			modelAgent.awaitTermination();
		} catch (InterruptedException e) {
			DeeCore.logError(e);
		}
	}
	
	protected void initializeModelManager() {
		// First of all, search for compilers on the path
		SearchAndAddCompilersOnPathJob compilerSearchJob = new SearchAndAddCompilersOnPathJob();
		compilerSearchJob.schedule();
		try {
			compilerSearchJob.join();
		} catch (InterruptedException ie) {
			// continue, we should still run rest of initialization
		}
		
		try {
			DeeCore.getWorkspace().run(new IWorkspaceRunnable() {
				@Override
				public void run(IProgressMonitor monitor) {
					DeeCore.getWorkspace().addResourceChangeListener(listener, IResourceChangeEvent.POST_CHANGE);
					initializeProjectsInfo(monitor);
				}
			}, null);
		} catch (CoreException e) {
			DeeCore.logError(e);
			// This really should not happen, but still try to recover by registering listener.
			DeeCore.getWorkspace().addResourceChangeListener(listener, IResourceChangeEvent.POST_CHANGE);
		}
	}
	
	protected void initializeProjectsInfo(@SuppressWarnings("unused") IProgressMonitor monitor) {
		try {
			IProject[] deeProjects = EclipseUtils.getOpenedProjects(DeeNature.NATURE_ID);
			for (IProject project : deeProjects) {
				startModelUpdateIfHasDubManifest(project);
			}
		} catch (CoreException ce) {
			DeeCore.logStatus(ce.getStatus());
		}
	}
	
	protected static final Path DUB_BUNDLE_PACKAGE_FILE = new Path(DubManifestParser.DUB_MANIFEST_FILENAME);
	
	protected void startModelUpdateIfHasDubManifest(IProject project) {
		IResource packageFile = project.findMember(DUB_BUNDLE_PACKAGE_FILE);
		if(packageFile != null && packageFile.getType() == IResource.FILE) {
			startDubProjectModelUpdate(project);
		}
	}
	
	protected final class DubProjectModelResourceListener implements IResourceChangeListener {
		
		@Override
		public void resourceChanged(IResourceChangeEvent resourceChange) {
			IResourceDelta workspaceDelta = resourceChange.getDelta();
			assertTrue(workspaceDelta != null);
			
			for (IResourceDelta projectDelta : workspaceDelta.getAffectedChildren()) {
				processProjectDelta(projectDelta);
			}
		}
		
		protected void processProjectDelta(IResourceDelta projectDelta) {
			//System.out.println("--- Got DELTA: " + EclipseUtils.printDelta(projectDelta));
			
			assertTrue(projectDelta.getResource().getType() == IResource.PROJECT);
			IProject project = (IProject) projectDelta.getResource();
			
			DubBundleDescription existingProjectModel = model.doGetBundleInfo(project.getName());
			
			if(projectDelta.getKind() == IResourceDelta.REMOVED || !DeeNature.isAcessible(project, true)) {
				if(existingProjectModel == null) {
					return; // Nothing to removed, might not have been a DUB model project.
				}
				startRemoveProjectUpdate(project);
				return;
			}
			
			switch(projectDelta.getKind()) {
			case IResourceDelta.ADDED:
				startModelUpdateIfHasDubManifest(project);
				break;
			case IResourceDelta.CHANGED:
				if((projectDelta.getFlags() & IResourceDelta.DESCRIPTION) != 0) {
					// It might be the case that project wasn't a DUB model project before, and now is.
					if(existingProjectModel == null) {
						// Then it's true, project has become DUB model project.
						startModelUpdateIfHasDubManifest(project);
						return;
					}
				}
				IResourceDelta[] resourceDeltas = projectDelta.getAffectedChildren();
				if(resourceDeltas == null)
					break;
				for (IResourceDelta resourceDelta : resourceDeltas) {
					if(resourceDelta.getResource().getType() == IResource.FILE && 
							resourceDelta.getProjectRelativePath().equals(DUB_BUNDLE_PACKAGE_FILE)) {
						if(resourceDelta.getResource().getType() == IResource.FILE) {
							startDubProjectModelUpdate(project);
						}
					}
				}
				break;
			}
		}

	}
	
	protected void startDubProjectModelUpdate(final IProject project) {
		log.println(">> Starting project update: ", project);
		
		DubBundleDescription unresolvedDescription = readUnresolvedBundleDescription(project);
		addProjectModel(project, unresolvedDescription);
		
		modelAgent.submit(new ProjectModelDubDescribeTask(this, project, unresolvedDescription));
	}
	
	protected DubBundleDescription readUnresolvedBundleDescription(final IProject project) {
		java.nio.file.Path location = project.getLocation().toFile().toPath();
		DubBundle unresolvedBundle = DubManifestParser.parseDubBundleFromLocation(location);
		
		return new DubBundleDescription(unresolvedBundle);
	}
	
	protected void startRemoveProjectUpdate(IProject project) {
		removeProjectModel(project);
		modelAgent.submit(new UpdateAllProjectsBuildpathTask(this, project));
	}
	
	/* ----------------------------------- */
	
	protected final void addProjectModel(IProject project, DubBundleDescription dubBundleDescription) {
		model.addProjectModel(project, dubBundleDescription);
	}
	
	protected final void removeProjectModel(IProject project) {
		model.removeProjectModel(project);
	}
	
	public void syncPendingUpdates() {
		modelAgent.waitForPendingTasks();
	}
	
	/** WARNING: this API is intended to be used for tests only */
	public ITaskAgent internal_getModelAgent() {
		return modelAgent;
	}
	
	public static IMarker[] getDubErrorMarkers(IProject project) throws CoreException {
		return project.findMarkers(DubModelManager.DUB_PROBLEM_ID, true, IResource.DEPTH_ONE);
	}
	
	protected abstract class DubModelManagerTask implements Runnable {
		
		protected final DubModelManager dubModelManager;
		
		public DubModelManagerTask() {
			this.dubModelManager = DubModelManager.this;
		}
		
		protected DubModel getModel() {
			return model;
		}
		
		protected void logInternalError(CoreException ce) {
			DeeCore.logError(ce);
		}
		
	}
	
}


class ProjectModelDubDescribeTask extends ProjectUpdateBuildpathTask implements IRunnableWithJob {
	
	protected final IProject project;
	protected final DubBundleDescription unresolvedDescription;
	
	protected ProjectModelDubDescribeTask(DubModelManager dubModelManager, IProject project, 
			DubBundleDescription unresolvedDescription) {
		super(dubModelManager);
		this.project = project;
		this.unresolvedDescription = unresolvedDescription;
	}
	
	@Override
	public void run() {
		deleteDubMarkers(project);

		// only run dub describe if unresolved description had no errors
		if(unresolvedDescription.hasErrors() == false) {
			try {
				EclipseAsynchJobAdapter.runUnderAsynchJob(getNameForJob(), this);
			} catch (InterruptedException e) {
				return;
			}
		} else {
			DubBundleException error = unresolvedDescription.getError();
			setDubErrorMarker(project, error.getMessage(), error.getCause());
		}
	}
	
	protected String getNameForJob() {
		return "Running 'dub describe' on project: " + project.getName();
	}
	
	protected void deleteDubMarkers(IProject project) {
		try {
			IMarker[] markers = DubModelManager.getDubErrorMarkers(project);
			for (IMarker marker : markers) {
				marker.delete();
			}
		} catch (CoreException ce) {
			DeeCore.logError(ce);
		}
	}
	
	protected void setDubErrorMarker(IProject project, String message, Throwable exception) {
		try {
			IMarker dubMarker = project.createMarker(DubModelManager.DUB_PROBLEM_ID);
			String messageExtra = exception == null ? "" : exception.getMessage();
			dubMarker.setAttribute(IMarker.MESSAGE, message + messageExtra);
			dubMarker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
		} catch (CoreException ce) {
			logInternalError(ce);
		}
	}
	
	
	@Override
	public void runUnderEclipseJob(IProgressMonitor monitor) {
		assertNotNull(monitor);
		updateProject(monitor);
	}
	
	protected Void updateProject(IProgressMonitor pm) {
		java.nio.file.Path location = project.getLocation().toFile().toPath();
		
		final DubProcessManager dubProcessManager = dubModelManager.dubProcessManager;
		ExternalProcessOutputHelper processHelper;
		try {
			String dubPath = DeeCorePreferences.getDubPath();
			processHelper = dubProcessManager.submitDubCommandAndWait(pm, project, dubPath, "describe");
		} catch (InterruptedException e) {
			// Should only happen during manager shutdown, so dont bother updating the model.
			return null;
		}  catch (CoreException ce) {
			return setProjectDubError(project, ce.getMessage(), ce.getCause());
		}
		
		String descriptionOutput;
		try {
			descriptionOutput = processHelper.getStdOutBytes().toString(StringUtil.UTF8);
		} catch (IOException e) {
			return setProjectDubError(project, "Error occurred reading dub process output: ", e);
		}
		
		int exitValue = processHelper.getProcess().exitValue();
		if(exitValue != 0) {
			return setProjectDubError(project, "dub returned non-zero status: " + exitValue, null);
		}
		
		// Trim leading characters. 
		// They shouldn't be there, but sometimes dub outputs non JSON text if downloading packages
		descriptionOutput = StringUtil.trimUntil('{', descriptionOutput);
		
		DubBundleDescription bundleDesc = DubDescribeParser.parseDescription(location, descriptionOutput);
		
		if(bundleDesc.hasErrors()) {
			setProjectDubError(project, "Error parsing description:", bundleDesc.getError());
		} else {
			dubModelManager.addProjectModel(project, bundleDesc);
			updateBuildpath(project, bundleDesc);
		}
		return null;
	}
	
	protected Void setProjectDubError(IProject project, String message, Throwable exception) {
		
		DubBundleException dubError = new DubBundleException(message, exception);
		
		dubModelManager.model.addErrorToProjectModel(project, dubError);
		
		setDubErrorMarker(project, message, exception);
		return null;
	}
	
}

abstract class ProjectUpdateBuildpathTask extends DubModelManagerTask {
	
	protected ProjectUpdateBuildpathTask(DubModelManager dubModelManager) {
		dubModelManager.super();
	}
	
	protected void updateBuildpath(IProject project, DubBundleDescription bundleDesc) {
		IScriptProject projectElement = DLTKCore.create(project);
		
		ArrayList<IBuildpathEntry> entries = new ArrayList<>();
		
		entries.add(DLTKCore.newContainerEntry(ScriptRuntime.newDefaultInterpreterContainerPath()));
		entries.add(DLTKCore.newContainerEntry(new Path(DubBuildpathContainerInitializer.ID)));
		
		for (java.nio.file.Path srcFolder : bundleDesc.getMainBundle().getEffectiveSourceFolders()) {
			IPath path2 = projectElement.getPath().append(srcFolder.toString());
			entries.add(DLTKCore.newSourceEntry(path2));
		}
		
		try {
			// TODO: should all this be set atomically? also, check if project exists
			updateDubBuildpathContainer(projectElement, getBuildpathEntriesFromDeps(bundleDesc));
			projectElement.setRawBuildpath(ArrayUtil.createFrom(entries, IBuildpathEntry.class), null);
			
		} catch (ModelException me) {
			logInternalError(me);
		}
	}
	
	protected void updateDubBuildpathContainer(IScriptProject projectElement, IBuildpathEntry[] entries) 
			throws ModelException {
		Path containerPath = new Path(DubBuildpathContainerInitializer.ID);
		DubContainer dubContainer = new DubContainer(containerPath, projectElement, entries);
		DLTKCore.setBuildpathContainer(containerPath, array(projectElement), array(dubContainer), null);
	}
	
	protected IBuildpathEntry[] getBuildpathEntriesFromDeps(DubBundleDescription bundleDesc) {
		ArrayList<IBuildpathEntry> depEntries = new ArrayList<>();
		for (DubBundle depBundle : bundleDesc.getBundleDependencies()) {
			if(depBundle.hasErrors()) {
				continue;
			}
			IProject workspaceProject = findProjectForBundle(depBundle);
			if(workspaceProject != null) {
				depEntries.add(DLTKCore.newProjectEntry(workspaceProject.getFullPath(), true));
			} else {
				for (java.nio.file.Path srcFolder : depBundle.getEffectiveSourceFolders()) {
					
					java.nio.file.Path srcFolderAbsolute = depBundle.location.resolve(srcFolder);
					assertTrue(srcFolderAbsolute.isAbsolute());
					depEntries.add(DubContainer.createDubBuildpathEntry(new Path(srcFolderAbsolute.toString())));
				}
			}
		}
		
		return ArrayUtil.createFrom(depEntries, IBuildpathEntry.class);
	}
	
	protected IProject findProjectForBundle(DubBundle depBundle) {
		return findProjectForBundleLocation(depBundle.location);
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
			if(project.getLocation().toFile().toPath().equals(location) && 
					dubModelManager.model.doGetBundleInfo(project.getName()) != null) {
				return project;
			}
		}
		
		return null;
	}
}

class UpdateAllProjectsBuildpathTask extends ProjectUpdateBuildpathTask {
	
	protected IProject removedProject;

	protected UpdateAllProjectsBuildpathTask(DubModelManager dubModelManager, IProject removedProject) {
		super(dubModelManager);
		this.removedProject = removedProject;
	}
	
	@Override
	public void run() {
		Set<String> dubProjects = dubModelManager.model.getDubProjects();
		for (String projectName : dubProjects) {
			// BM: we could optimize this, and update only if removed project was in buildpath of project to update.
			DubBundleDescription bundleDesc = dubModelManager.model.doGetBundleInfo(projectName);
			IProject project = DeeCore.getWorkspaceRoot().getProject(projectName);
			updateBuildpath(project, bundleDesc);
		}
	}
	
}