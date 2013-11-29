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

import java.net.URI;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.misc.ExecutorAgent;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.LangCore;

import org.dsource.ddt.ide.core.DeeNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
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
import dtool.dub.DubBundleParser;

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
	protected final DubProjectModelListener listener;
	protected final HashMap<String, DubBundle> dubBundleInfos = new HashMap<>();
	
	public DubProjectModel() throws CoreException {
		listener = new DubProjectModelListener();
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
			addNewProject(deeProject);
		}
	}
	
	protected void addNewProject(final IScriptProject projectElement) {
		log.println(">> Adding project: ", projectElement);
		String projectName = projectElement.getElementName();
		URI locationURI = projectElement.getResource().getLocationURI();
		
		final DubBundle dubBundle = DubBundleParser.parseDubBundleFromLocation(Paths.get(locationURI));
		
		dubBundleInfos.put(projectName, dubBundle);
		
		executorAgent.submit(new Runnable() {
			@Override
			public void run() {
				setBundleDo(projectElement, dubBundle);
			}
		});
	}
	
	private Status setBundleDo(final IScriptProject projectElement, final DubBundle dubBundle) {
		final String projectName = projectElement.getElementName();
		
		if(dubBundle.error == null) {
			ArrayList<IBuildpathEntry> entries = new ArrayList<>();
			
			// TODO: correlate the compiler install used by Dub with the one on buildpath
			entries.add(DLTKCore.newContainerEntry(ScriptRuntime.newDefaultInterpreterContainerPath()));
			
			for (java.nio.file.Path srcFolder : dubBundle.getEffectiveSourceFolders()) {
				IPath path = projectElement.getPath().append(srcFolder.toString());
				entries.add(DLTKCore.newSourceEntry(path));
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
	
	protected void removedProject(IScriptProject projectElement) {
		log.println(">> Removing project: ", projectElement);
		String projectName = projectElement.getElementName();
		dubBundleInfos.remove(projectName);
	}
	
	protected static final Path DUB_BUNDLE_PACKAGE_FILE = new Path("package.json");
	
	protected final class DubProjectModelListener implements IElementChangedListener {
		
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
		
		private void processScriptProjectDelta(IModelElementDelta projectDelta) {
			IScriptProject projectElement = (IScriptProject) projectDelta.getElement();
			
			switch(projectDelta.getKind()) {
			case IModelElementDelta.ADDED:
				addNewProject(projectElement);
				break;
			case IModelElementDelta.REMOVED:
				removedProject(projectElement);
				break;
			case IModelElementDelta.CHANGED:
				IResourceDelta[] resourceDeltas = projectDelta.getResourceDeltas();
				if(resourceDeltas == null)
					break;
				for (IResourceDelta resourceDelta : resourceDeltas) {
					if(resourceDelta.getResource().getType() == IResource.FILE && 
							resourceDelta.getProjectRelativePath().equals(DUB_BUNDLE_PACKAGE_FILE)) {
						if(resourceDelta.getResource().getType() == IResource.FILE) {
							addNewProject(projectElement);
						}
					}
				}
				break;
			default:
				assertFail("Unknown delta type for project;");
			}
		}

	}
	
	public void syncPendingUpdates() {
		executorAgent.waitForPendingTasks();
	}
	
}