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
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.CoreUtil.array;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.misc.ExecutorAgent;
import melnorme.utilbox.misc.ProcessHelper;
import melnorme.utilbox.misc.StringUtil;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.LangCore;

import org.dsource.ddt.ide.core.DeeNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
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
import dtool.dub.DubBundle.DubBundleDescription;
import dtool.dub.DubBundleDescriptionParser;

public class DubProjectModel {
	
	protected static SimpleLogger log = new SimpleLogger(true);
	
	public static DubProjectModel defaultInstance;
	
	public static void initializeDefault() throws CoreException {
		defaultInstance = new DubProjectModel();
	}
	
	public static DubProjectModel getDefault() {
		return defaultInstance;
	}
	
	protected final ExecutorAgent executorAgent = new ExecutorAgent(DubProjectModel.class.getSimpleName());
	protected final DubProjectModelResourceListener listener;
	protected final HashMap<String, DubBundleDescription> dubBundleInfos = new HashMap<>();
	
	public DubProjectModel() throws CoreException {
		listener = new DubProjectModelResourceListener();
		DLTKCore.run(new IWorkspaceRunnable() {
			
			@Override
			public void run(IProgressMonitor monitor) {
				initializeProjectInfo();
				DLTKCore.addElementChangedListener(listener, ElementChangedEvent.POST_CHANGE);
			}
			
		}, null);
	}
	
	public void dispose() {
		DLTKCore.removeElementChangedListener(listener);
		if(defaultInstance == this) {
			defaultInstance = null;
		}
		executorAgent.shutdown();
	}
	
	protected void initializeProjectInfo() {
		IScriptProject[] deeProjects;
		try {
			deeProjects = LangCore.getDLTKModel().getScriptProjects(DeeNature.NATURE_ID);
		} catch (ModelException e) {
			DeeCore.log(e);
			return;
		}
		
		for (IScriptProject deeProject : deeProjects) {
			queueProjectUpdate(deeProject);
		}
	}
	
	
	protected synchronized void addProject(IScriptProject projectElement, DubBundleDescription dubBundle) {
		log.println(">> Add project info: ", projectElement);
		dubBundleInfos.put(projectElement.getElementName(), dubBundle);
	}
	
	protected synchronized void removeProject(IScriptProject projectElement) {
		log.println(">> Removing project: ", projectElement);
		dubBundleInfos.remove(projectElement.getElementName());
	}
	
	public synchronized DubBundleDescription getBundleInfo(String projectName) {
		return dubBundleInfos.get(projectName);
	}
	
	
	protected static final Path DUB_BUNDLE_PACKAGE_FILE = new Path("package.json");
	
	protected final class DubProjectModelResourceListener implements IElementChangedListener {
		
		@Override
		public void elementChanged(ElementChangedEvent event) {
			IModelElementDelta delta = event.getDelta();
			System.out.println("----->\n" + delta);
			
			assertTrue(delta.getElement().getElementType() == IModelElement.SCRIPT_MODEL);
			
			IModelElementDelta[] affectedChildren = delta.getAffectedChildren();
			for (IModelElementDelta projectElementDelta : affectedChildren) {
				
				assertTrue(projectElementDelta.getElement().getElementType() == IModelElement.SCRIPT_PROJECT);
				processScriptProjectDelta(projectElementDelta);
			}
		}
		
		protected void processScriptProjectDelta(IModelElementDelta projectDelta) {
			IScriptProject projectElement = (IScriptProject) projectDelta.getElement();
			
			switch(projectDelta.getKind()) {
			case IModelElementDelta.ADDED:
				queueProjectUpdate(projectElement);
				break;
			case IModelElementDelta.REMOVED:
				removeProject(projectElement);
				break;
			case IModelElementDelta.CHANGED:
				IResourceDelta[] resourceDeltas = projectDelta.getResourceDeltas();
				if(resourceDeltas == null)
					break;
				for (IResourceDelta resourceDelta : resourceDeltas) {
					if(resourceDelta.getResource().getType() == IResource.FILE && 
							resourceDelta.getProjectRelativePath().equals(DUB_BUNDLE_PACKAGE_FILE)) {
						if(resourceDelta.getResource().getType() == IResource.FILE) {
							queueProjectUpdate(projectElement);
						}
					}
				}
				break;
			default:
				assertFail("Unknown delta type for project;");
			}
		}
		
	}
	
	protected void queueProjectUpdate(final IScriptProject projectElement) {
		log.println(">> Updating project: ", projectElement);
		
		executorAgent.submit(new UpdateProjectModel(projectElement, this));
	}
	
	public void syncPendingUpdates() {
		executorAgent.waitForPendingTasks();
	}
	
}

class UpdateProjectModel implements Runnable {
	
	protected final IScriptProject projectElement;
	protected DubProjectModel dubProjectModel;
	
	protected UpdateProjectModel(IScriptProject projectElement, DubProjectModel dubProjectModel) {
		this.projectElement = projectElement;
		this.dubProjectModel = dubProjectModel;
	}
	
	@Override
	public void run() {
		IStatus status = updateProject(projectElement);
		if(!status.isOK()) {
			// TODO report this to user.
			DeeCore.getInstance().getLog().log(status);
		}
	}
	
	protected IStatus updateProject(IScriptProject projectElement) {
		String projectName = projectElement.getElementName();
		URI locationURI = projectElement.getResource().getLocationURI();
		
		DubBundleDescription bundleDesc;
		try {
			// TODO handle DUB error codes
			String bundleDescription = runDubDescribe(Paths.get(locationURI));
			bundleDesc = new DubBundleDescriptionParser().parseDescription(bundleDescription);
		} catch (CoreException ce) {
			return ce.getStatus();
		}
		
		if(!bundleDesc.hasErrors()) {
			
			dubProjectModel.addProject(projectElement, bundleDesc); 

			ArrayList<IBuildpathEntry> entries = new ArrayList<>();
			
			// TODO: correlate the compiler install used by Dub with the one on buildpath
			entries.add(DLTKCore.newContainerEntry(ScriptRuntime.newDefaultInterpreterContainerPath()));
			
			entries.add(DLTKCore.newContainerEntry(new Path(DubContainerInitializer.ID)));
			
			for (java.nio.file.Path srcFolder : bundleDesc.getMainBundle().getRawSourceFolders()) {
				IPath path = projectElement.getPath().append(srcFolder.toString());
				entries.add(DLTKCore.newSourceEntry(path));
			}
			
			try {
				updateDubContainer(projectElement, getBuildpathEntriesFromDesc(bundleDesc));
			} catch (ModelException me) {
				return me.getStatus();
			}
			
			try {
				projectElement.setRawBuildpath(ArrayUtil.createFrom(entries, IBuildpathEntry.class), null);
			} catch (ModelException e) {
				return DeeCore.createErrorStatus(
						"Failure updating buildpath of project " + projectName + ".", e);
			}
		}
		return DeeCore.createStatus(null);
	}

	protected void updateDubContainer(IScriptProject projectElement, IBuildpathEntry[] entries) throws ModelException {
		Path containerPath = new Path(DubContainerInitializer.ID);
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
			for (java.nio.file.Path srcFolder : depBundle.getRawSourceFolders()) {
				
				java.nio.file.Path srcFolderAbsolute = depBundle.location.resolve(srcFolder);
				assertTrue(srcFolderAbsolute.isAbsolute());
				IPath path = new Path(srcFolderAbsolute.toString());
				depEntries.add(DubContainer.createDubBuildpathEntry(path));
			}
		}
		
		return ArrayUtil.createFrom(depEntries, IBuildpathEntry.class);
	}
	
	protected String runDubDescribe(java.nio.file.Path path) throws CoreException {
		assertTrue(path.isAbsolute());
		ProcessBuilder pb = new ProcessBuilder("dub", "describe");
		pb.directory(path.toFile());
		
		ProcessHelper processHelper = new ProcessHelper();
		try {
			processHelper.startProcess(pb);
		} catch (IOException e) {
			throw DeeCore.createCoreException("Could not start dub process", e);
		}
		
		try {
			processHelper.awaitTermination(2000);
		} catch (InterruptedException e) {
			throw DeeCore.createCoreException("Timeout running dub process", e);
		}
		
		try {
			return processHelper.getStdOutBytes().toString(StringUtil.UTF8);
		} catch (IOException e) {
			throw DeeCore.createCoreException("Error reading dub output: ", e);
		}
		
	}
	
}