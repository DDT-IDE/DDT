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
package mmrnmhrm.core.dub_model;

import static melnorme.lang.ide.core.utils.TextMessageUtils.headerBIG;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.CoreUtil.array;

import java.text.MessageFormat;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

import dtool.dub.BundlePath;
import dtool.dub.DubBundle;
import dtool.dub.DubBundle.DubBundleException;
import dtool.dub.DubBundleDescription;
import dtool.dub.DubHelper;
import dtool.dub.DubManifestParser;
import dtool.engine.compiler_installs.CompilerInstall;
import dtool.engine.compiler_installs.SearchCompilersOnPathOperation;
import melnorme.lang.ide.core.LangCore;
import melnorme.lang.ide.core.operations.AbstractToolManager;
import melnorme.lang.ide.core.operations.MessageEventInfo;
import melnorme.lang.ide.core.operations.OperationInfo;
import melnorme.lang.ide.core.project_model.BundleModelManager;
import melnorme.lang.ide.core.project_model.LangBundleModel;
import melnorme.lang.ide.core.utils.EclipseUtils;
import melnorme.lang.ide.core.utils.ResourceUtils;
import melnorme.lang.ide.core.utils.operation.CoreOperationRunnable;
import melnorme.lang.ide.core.utils.operation.EclipseAsynchJobAdapter;
import melnorme.lang.ide.core.utils.operation.EclipseAsynchJobAdapter.IRunnableWithJob;
import melnorme.lang.ide.core.utils.process.IRunProcessTask;
import melnorme.utilbox.concurrency.ITaskAgent;
import melnorme.utilbox.concurrency.OperationCancellation;
import melnorme.utilbox.core.CommonException;
import melnorme.utilbox.process.ExternalProcessHelper.ExternalProcessResult;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.DeeCoreMessages;
import mmrnmhrm.core.dub_model.DeeBundleModelManager.DeeBundleModel;
import mmrnmhrm.core.dub_model.DeeBundleModelManager.WorkspaceModelManagerTask;
import mmrnmhrm.core.engine.DeeToolManager;

/**
 * Updates a {@link DeeBundleModel} when resource changes occur, using 'dub describe'.
 * Also creates problem markers on the Eclipse workspace. 
 */
public class DeeBundleModelManager extends BundleModelManager<DubBundleInfo, DeeBundleModel> {
	
	public static class DeeBundleModel extends LangBundleModel<DubBundleInfo> {
		
	}
	
	/* -----------------  ----------------- */
	
	public static final String DUB_PROBLEM_ID = DeeCore.PLUGIN_ID + ".DubProblem";
	
	public DeeBundleModelManager() {
		super(new DeeBundleModel());
	}
	
	public DeeToolManager getProcessManager() {
		return (DeeToolManager) LangCore.getToolManager();
	}
	
	@Override
	protected ManagerResourceListener init_createResourceListener() {
		return new ManagerResourceListener(ResourceUtils.epath(BundlePath.DUB_MANIFEST_Path));
	}
	
	@Override
	protected void bundleProjectAdded(IProject project) {
		beginProjectDescribeUpdate(project);
	}
	
	@Override
	protected void bundleManifestFileChanged(final IProject project) {
		beginProjectDescribeUpdate(project);
	}
	
	@Override
	protected void bundleProjectRemoved(IProject project) {
		/* FIXME: BUG here: updates to model should only occur in model agent. */
		model.removeProjectInfo(project);
	}
	
	@Override
	protected DubBundleInfo createNewInfo(IProject project) {
		DubBundleDescription unresolvedDescription = readUnresolvedBundleDescription(project);
		return addProjectInfo(project, unresolvedDescription);
	}
	
	protected void beginProjectDescribeUpdate(final IProject project) {
		DubBundleInfo unresolvedProjectInfo = createNewInfo(project);
		
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
	
	protected final DubBundleInfo addProjectInfo(IProject project, DubBundleDescription dubBundleDescription) {
		CompilerInstall compilerInstall = new SearchCompilersOnPathOperation_Eclipse().
				searchForCompilersInDefaultPathEnvVars().getPreferredInstall();
		
		return getModel().setProjectInfo(project, new DubBundleInfo(compilerInstall, dubBundleDescription));
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
		
		protected final DeeBundleModelManager workspaceModelManager;
		
		public WorkspaceModelManagerTask() {
			this.workspaceModelManager = DeeBundleModelManager.this;
		}
		
		protected void logInternalError(CoreException ce) {
			DeeCore.logInternalError(ce);
		}
		
	}
	
}


class ProjectModelDubDescribeTask extends ProjectUpdateBuildpathTask implements IRunnableWithJob {
	
	protected final IProject project;
	protected final DubBundleInfo unresolvedProjectInfo;
	protected final  DubBundleDescription unresolvedDescription;
	
	protected ProjectModelDubDescribeTask(DeeBundleModelManager dubModelManager, IProject project, 
			DubBundleInfo unresolvedProjectInfo) {
		super(dubModelManager);
		this.project = project;
		this.unresolvedProjectInfo = unresolvedProjectInfo;
		unresolvedDescription = unresolvedProjectInfo.getBundleDesc();
	}
	
	protected DeeToolManager getProcessManager() {
		return workspaceModelManager.getProcessManager();
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
		IMarker[] markers = DeeBundleModelManager.getDubErrorMarkers(project);
		for (IMarker marker : markers) {
			marker.delete();
		}
	}
	
	protected void setDubErrorMarker(IProject project, DubBundleException error) throws CoreException {
		setDubErrorMarker(project, error.getExtendedMessage());
	}
	
	protected void setDubErrorMarker(IProject project, String message) throws CoreException {
		IMarker dubMarker = project.createMarker(DeeBundleModelManager.DUB_PROBLEM_ID);
		dubMarker.setAttribute(IMarker.MESSAGE, message);
		dubMarker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
	}
	
	@Override
	public void runUnderEclipseJob(IProgressMonitor monitor) {
		assertNotNull(monitor);
		try {
			new CoreOperationRunnable() {
				@Override
				public void doRun(IProgressMonitor pm) throws CommonException, CoreException, OperationCancellation {
					resolveProjectOperation(pm);
				}
			}.coreAdaptedRun(monitor);
		} catch(OperationCancellation ce) {
			return;
		} catch(CoreException ce) {
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
	
	protected AbstractToolManager getToolManager() {
		return LangCore.getToolManager();
	}
	
	protected Void resolveProjectOperation(IProgressMonitor pm) throws CoreException, CommonException {
		IPath projectLocation = project.getLocation();
		if(projectLocation == null) {
			return null; // Project no longer exists, or not stored in the local filesystem.
		}
		
		BundlePath bundlePath = BundlePath.create(projectLocation.toFile().toPath());
			
		String dubPath = getToolManager().getSDKToolPath().toString();
		
		OperationInfo opInfo = getToolManager().startNewToolOperation();
		getProcessManager().notifyMessageEvent(new MessageEventInfo(opInfo, 
			headerBIG(MessageFormat.format(DeeCoreMessages.RunningDubDescribe, project.getName()))));
		
		ProcessBuilder pb = AbstractToolManager.createProcessBuilder(project, array(dubPath, "describe"));
		IRunProcessTask dubDescribeTask = getProcessManager().newRunToolTask(opInfo, pb, pm);
		
		ExternalProcessResult processHelper;
		try {
			processHelper = getProcessManager().submitTaskAndAwaitResult(dubDescribeTask);
		} catch (OperationCancellation e) {
			throw LangCore.createCoreException("Error, `describe` cancelled.", null);
		}
		
		final DubBundleDescription bundleDesc = DubHelper.parseDubDescribe(bundlePath, processHelper);
		if(bundleDesc.hasErrors()) {
			throw LangCore.createCoreException("Error resolving bundle: ", bundleDesc.getError());
		}
		
		ResourceUtils.getWorkspace().run(new IWorkspaceRunnable() {
			@Override
			public void run(IProgressMonitor monitor) throws CoreException {
				if(project.exists() == false) {
					return;
				}
				assertTrue(!bundleDesc.hasErrors());
				
				workspaceModelManager.addProjectInfo(project, bundleDesc);
			}
		}, null, 0, pm);
		
		return null;
	}
	
	protected void setProjectDubError(IProject project, CoreException ce) throws CoreException {
		
		DubBundleException dubError = new DubBundleException(ce.getMessage(), ce.getCause());
		
		DubBundle main = unresolvedDescription.getMainBundle();
		DubBundleDescription bundleDesc = new DubBundleDescription(main, dubError);
		DubBundleInfo newProjectInfo = new DubBundleInfo(unresolvedProjectInfo.compilerInstall, bundleDesc);
		workspaceModelManager.getModel().setProjectInfo(project, newProjectInfo);
		
		setDubErrorMarker(project, dubError);
	}
	
}

abstract class ProjectUpdateBuildpathTask extends WorkspaceModelManagerTask {
	
	protected ProjectUpdateBuildpathTask(DeeBundleModelManager dubModelManager) {
		dubModelManager.super();
	}
	
}