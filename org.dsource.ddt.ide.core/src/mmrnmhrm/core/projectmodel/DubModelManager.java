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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.CoreUtil.array;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

import melnorme.lang.ide.core.LangCore;
import melnorme.lang.ide.core.utils.EclipseUtils;
import melnorme.utilbox.concurrency.ExternalProcessOutputHelper;
import melnorme.utilbox.concurrency.IExecutorAgent;
import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.misc.ListenerListHelper;
import melnorme.utilbox.misc.StringUtil;
import mmrnmhrm.core.CoreExecutorAgent;
import mmrnmhrm.core.DeeCore;

import org.dsource.ddt.ide.core.DeeNature;
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
import org.eclipse.dltk.core.ElementChangedEvent;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IElementChangedListener;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IModelElementDelta;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.launching.ScriptRuntime;

import dtool.SimpleLogger;
import dtool.dub.DubBundle;
import dtool.dub.DubBundle.DubBundleException;
import dtool.dub.DubBundleDescription;
import dtool.dub.DubDescribeParser;
import dtool.dub.DubManifestParser;

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
	
	public static final String DUB_PROBLEM_ID = DeeCore.EXTENSIONS_IDPREFIX + "DubProblem";
	
	/** Marker interface for listener callbacks that runs in the Dub executor. 
	 * Used for documentation purposes only, has no effect in code. */
	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.SOURCE)
	public static @interface RunsInDubExecutor { }
	
	/* ----------------------------------- */
	
	protected final DubModel model;
	protected final IExecutorAgent executorAgent = new CoreExecutorAgent(DubModelManager.class.getSimpleName());
	protected final DubProjectModelResourceListener listener = new DubProjectModelResourceListener();
	protected boolean started = false;
	
	public DubModelManager(DubModel model) {
		this.model = model;
	}
	
	public void startManager() {
		assertTrue(started == false); // start only once
		started = true;
		
		// Run heavyweight initialization in executor thread.
		// This is necessary so that we avoid running the initialization during plugin initialization.
		// Otherwise there could be problems because initialization is heavyweight code:
		// it requests workspace locks (which may not be available) and issues workspace deltas
		executorAgent.submit(new Runnable() {
			@Override
			public void run() {
				initializeModelManager();
			}
		});
	}
	
	public void shutdownManager() {
		// It is possible to shutdown the manager without having it started.
		
		DLTKCore.removeElementChangedListener(listener);
		executorAgent.shutdownNow();
		try {
			executorAgent.awaitTermination();
		} catch (InterruptedException e) {
			DeeCore.log(e);
		}
	}
	
	@RunsInDubExecutor
	protected void initializeModelManager() {
		try {
			DLTKCore.run(new IWorkspaceRunnable() {
				@Override
				public void run(IProgressMonitor monitor) {
					DLTKCore.addElementChangedListener(listener, ElementChangedEvent.POST_CHANGE);
					readProjectInfo(monitor);
				}
			}, null);
		} catch (CoreException e) {
			DeeCore.logError(e);
			// This really should not happen, but still try to recover by registering listener.
			DLTKCore.addElementChangedListener(listener, ElementChangedEvent.POST_CHANGE);
		}
	}
	
	protected void readProjectInfo(@SuppressWarnings("unused") IProgressMonitor monitor) {
		try {
			IProject[] deeProjects = EclipseUtils.getOpenedProjects(DeeNature.NATURE_ID);
			for (IProject project : deeProjects) {
				checkNewProject(project);
			}
		} catch (CoreException e) {
			DeeCore.log(e);
		}
	}
	
	
	protected static final Path DUB_BUNDLE_PACKAGE_FILE = new Path(DubManifestParser.DUB_MANIFEST_FILENAME);
	
	protected final class DubProjectModelResourceListener implements IElementChangedListener {
		
		@Override
		public void elementChanged(ElementChangedEvent event) {
			IModelElementDelta delta = event.getDelta();
			
			assertTrue(delta.getElement().getElementType() == IModelElement.SCRIPT_MODEL);
			
			IModelElementDelta[] affectedChildren = delta.getAffectedChildren();
			for (IModelElementDelta projectElementDelta : affectedChildren) {
				
				assertTrue(projectElementDelta.getElement().getElementType() == IModelElement.SCRIPT_PROJECT);
				processScriptProjectDelta(projectElementDelta);
			}
		}
		
		protected void processScriptProjectDelta(IModelElementDelta projectDelta) {
			IScriptProject projectElement = (IScriptProject) projectDelta.getElement();
			IProject project = projectElement.getProject();
			
			switch(projectDelta.getKind()) {
			case IModelElementDelta.ADDED:
				checkNewProject(project);
				break;
			case IModelElementDelta.REMOVED:
				removeProjectModel(project);
				break;
			case IModelElementDelta.CHANGED:
				IResourceDelta[] resourceDeltas = projectDelta.getResourceDeltas();
				if(resourceDeltas == null)
					break;
				for (IResourceDelta resourceDelta : resourceDeltas) {
					if(resourceDelta.getResource().getType() == IResource.FILE && 
							resourceDelta.getProjectRelativePath().equals(DUB_BUNDLE_PACKAGE_FILE)) {
						if(resourceDelta.getResource().getType() == IResource.FILE) {
							updateProjectDubModel(project);
						}
					}
				}
				break;
			default:
				assertFail("Unknown delta type for project;");
			}
		}
		
	}
	
	protected void checkNewProject(IProject project) {
		IResource packageFile = project.findMember(DUB_BUNDLE_PACKAGE_FILE);
		if(packageFile != null && packageFile.getType() == IResource.FILE) {
			updateProjectDubModel(project);
		}
	}
	
	protected void updateProjectDubModel(final IProject project) {
		log.println(">> Updating project: ", project);
		
		DubBundleDescription unresolvedDescription = readUnresolvedBundleDescription(project);
		// only run dub describe if unresolved description had no errors
		if(unresolvedDescription.hasErrors() == false) {
			queueDubDescribeJob(project);
		}
	}
	
	protected void queueDubDescribeJob(final IProject project) {
		executorAgent.submit(new DubDescribeUpdateProjectTask(project, this));
	}
	
	protected DubBundleDescription readUnresolvedBundleDescription(final IProject project) {
		java.nio.file.Path location = project.getLocation().toFile().toPath();
		DubBundle unresolvedBundle = DubManifestParser.parseDubBundleFromLocation(location);
		
		DubBundleDescription dubBundleDescription = new DubBundleDescription(unresolvedBundle);
		addProjectModel(project, dubBundleDescription);
		return dubBundleDescription;
	}
	
	protected void addProjectModel(IProject project, DubBundleDescription dubBundleDescription) {
		model.addProjectModel(project, dubBundleDescription);
	}
	
	protected void removeProjectModel(IProject project) {
		model.removeProjectModel(project);
	}
	
	public void syncPendingUpdates() {
		executorAgent.waitForPendingTasks();
	}
	
	/** WARNING: this API is for test use only */
	public IExecutorAgent internal_getExecutorAgent() {
		return executorAgent;
	}
	
	public static IMarker[] getDubErrorMarkers(IProject project) throws CoreException {
		return project.findMarkers(DubModelManager.DUB_PROBLEM_ID, true, IResource.DEPTH_ONE);
	}
	
	/* ----------------------------------- */
	
	protected final ListenerListHelper<IDubProcessListener> dubProcessListenersHelper = new ListenerListHelper<>();
	
	public void addDubProcessListener(IDubProcessListener dubProcessListener) {
		dubProcessListenersHelper.addListener(dubProcessListener);
	}
	
	public void removeDubProcessListener(IDubProcessListener dubProcessListener) {
		dubProcessListenersHelper.removeListener(dubProcessListener);
	}
	
}


class DubExternalProcessHelper extends ExternalProcessOutputHelper {
	
	protected IProgressMonitor monitor;
	
	public DubExternalProcessHelper(Process process, IProgressMonitor monitor, boolean startReaders) {
		super(process, true, startReaders);
		this.monitor = monitor;
	}
	
	@Override
	protected boolean isCanceled() {
		return monitor.isCanceled();
	}
	
	@Override
	protected void handleListenerException(RuntimeException e) {
		LangCore.logError(e, "Internal error notifying listener");
	}
	
}

class DubDescribeUpdateProjectTask extends RunnableWithEclipseAsynchJob {
	
	protected final IProject project;
	protected final DubModelManager dubModelManager;
	
	protected DubDescribeUpdateProjectTask(IProject project, DubModelManager dubModelManager) {
		this.project = project;
		this.dubModelManager = dubModelManager;
	}
	
	@Override
	protected String getNameForJob() {
		return "Running 'dub describe' on project: " + project.getName();
	}
	
	@Override
	protected void runWithMonitor(IProgressMonitor monitor) {
		assertNotNull(monitor);
		updateProject(monitor);
	}
	
	protected void logInternalError(CoreException ce) {
		DeeCore.logError(ce);
	}
	
	protected Void updateProject(IProgressMonitor monitor) {
		
		deleteDubMarkers(project);
		
		java.nio.file.Path location = project.getLocation().toFile().toPath();
		assertTrue(location.isAbsolute());
		ProcessBuilder pb = new ProcessBuilder("dub", "describe").directory(location.toFile());
		
		ExternalProcessOutputHelper processHelper;
		try {
			processHelper = new DubExternalProcessHelper(pb.start(), monitor, false);
		} catch (IOException e) {
			return setProjectDubError(project, "Could not start dub process: ",  e);
		}
		
		notifyDubProcessStarted(processHelper, pb);		
		processHelper.startReaderThreads();
		
		try {
			processHelper.awaitTerminationStrict_destroyOnException();
		} catch (InterruptedException e) {
			if(monitor.isCanceled()) {
				return setProjectDubError(project, "Cancelled dub process.", null);
			}
			return setProjectDubError(project, "Interrupted running dub process.", null);
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
		
		
		DubBundleDescription bundleDesc = DubDescribeParser.parseDescription(location, descriptionOutput);
		
		if(!bundleDesc.hasErrors()) {
			updateBuildpath(project, bundleDesc);
		} else {
			setProjectDubError(project, "Error parsing description:", bundleDesc.getError());
		}
		return null;
	}
	
	public void notifyDubProcessStarted(ExternalProcessOutputHelper processHelper, ProcessBuilder pb) {
		List<IDubProcessListener> listeners = dubModelManager.dubProcessListenersHelper.getListeners();
		for (IDubProcessListener dubProcessListener : listeners) {
			dubProcessListener.handleProcessStarted(processHelper, pb);
		}
	}
	
	protected void deleteDubMarkers(IProject project) {
		try {
			IMarker[] markers = DubModelManager.getDubErrorMarkers(project);
			for (IMarker marker : markers) {
				marker.delete();
			}
		} catch (CoreException ce) {
			logInternalError(ce);
		}
	}
	
	protected Void setProjectDubError(IProject project, String message, Exception exception) {
		
		DubBundleException dubError = new DubBundleException(message, exception);
		
		dubModelManager.model.addErrorToProjectModel(project, dubError);
		
		try {
			IMarker dubMarker = project.createMarker(DubModelManager.DUB_PROBLEM_ID);
			String messageExtra = exception == null ? "" : exception.getMessage();
			dubMarker.setAttribute(IMarker.MESSAGE, message + messageExtra);
			dubMarker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
		} catch (CoreException ce) {
			logInternalError(ce);
		}
		return null;
	}
	
	protected void updateBuildpath(IProject project, DubBundleDescription bundleDesc) {
		IScriptProject projectElement = DLTKCore.create(project);
		
		ArrayList<IBuildpathEntry> entries = new ArrayList<>();
		
		// TODO: correlate the compiler install used by Dub with the one on buildpath
		entries.add(DLTKCore.newContainerEntry(ScriptRuntime.newDefaultInterpreterContainerPath()));
		
		entries.add(DLTKCore.newContainerEntry(new Path(DubBuildpathContainerInitializer.ID)));
		
		for (java.nio.file.Path srcFolder : bundleDesc.getMainBundle().getSourceFolders()) {
			IPath path2 = projectElement.getPath().append(srcFolder.toString());
			entries.add(DLTKCore.newSourceEntry(path2));
		}
		
		try {
			// TODO: should all this be set atomically?
			updateDubContainer(projectElement, getBuildpathEntriesFromDesc(bundleDesc));
			projectElement.setRawBuildpath(ArrayUtil.createFrom(entries, IBuildpathEntry.class), null);
			
			dubModelManager.addProjectModel(project, bundleDesc);
		} catch (ModelException me) {
			logInternalError(me);
		}
	}
	
	protected void updateDubContainer(IScriptProject projectElement, IBuildpathEntry[] entries) 
			throws ModelException {
		Path containerPath = new Path(DubBuildpathContainerInitializer.ID);
		DubContainer dubContainer = new DubContainer(containerPath, projectElement, entries);
		DLTKCore.setBuildpathContainer(containerPath, array(projectElement), array(dubContainer), null);
	}
	
	protected static IBuildpathEntry[] getBuildpathEntriesFromDesc(DubBundleDescription bundleDesc) {
		ArrayList<IBuildpathEntry> depEntries = new ArrayList<>();
		for (DubBundle depBundle : bundleDesc.getBundleDependencies()) {
			if(depBundle.hasErrors()) {
				continue;
			}
			
			// TODO project dependencies
			for (java.nio.file.Path srcFolder : depBundle.getSourceFolders()) {
				
				java.nio.file.Path srcFolderAbsolute = depBundle.location.resolve(srcFolder);
				assertTrue(srcFolderAbsolute.isAbsolute());
				IPath path = new Path(srcFolderAbsolute.toString());
				depEntries.add(DubContainer.createDubBuildpathEntry(path));
			}
		}
		
		return ArrayUtil.createFrom(depEntries, IBuildpathEntry.class);
	}
	
}