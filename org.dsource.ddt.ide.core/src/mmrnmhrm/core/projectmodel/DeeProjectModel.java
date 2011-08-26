package mmrnmhrm.core.projectmodel;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.CoreUtil.tryCast;

import java.util.HashMap;
import java.util.Map;

import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.LangCore;
import mmrnmhrm.core.launch.DmdInstall;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.ElementChangedEvent;
import org.eclipse.dltk.core.IElementChangedListener;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IModelElementDelta;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.ScriptRuntime;

import dtool.SimpleLogger;

/**
 * The D Model. 
 * It is a listener for the DLTK Model, and maintains a map of DeeProjectOptions.
 * XXX: We have to solve some concurrency and binding issues here
 */
public class DeeProjectModel implements IElementChangedListener {
	
	protected static SimpleLogger projectModelLog = new SimpleLogger(false);
	
	private static DeeProjectModel instance;
	
	public static void initializeModel() {
		instance = new DeeProjectModel();
		DLTKCore.addElementChangedListener(instance, ElementChangedEvent.POST_CHANGE);
	}
	
	public static void dispose() {
		DLTKCore.removeElementChangedListener(instance);
	}
	
	Map<IScriptProject, DeeProjectOptions> deeInfos;
	
	public DeeProjectModel() {
		deeInfos = new HashMap<IScriptProject, DeeProjectOptions>(); 
	}
	
	@Override
	public void elementChanged(ElementChangedEvent event) {
		IModelElementDelta delta= event.getDelta();
		if (delta != null) {
			projectModelLog.println("delta received: ");
			projectModelLog.println(delta);
		}
		IModelElement element = delta.getElement();
		
		if(element.getElementType() == IModelElement.SCRIPT_MODEL) {
			for (IModelElementDelta projectdelta : delta.getAffectedChildren()) {
				processProjectDelta(projectdelta);
			}
		} else {
			assertFail("Delta root must be model");
		}
	}
	
	private void processProjectDelta(IModelElementDelta delta) {
		IScriptProject project = (IScriptProject) delta.getElement();
		switch(delta.getKind()) {
		case IModelElementDelta.ADDED:
			projectModelLog.println(">> Adding project: ", project);
			addNewDLTKProjectConfig(project);
			break;
		case IModelElementDelta.REMOVED:
			projectModelLog.println(">> Removing project: ", project);
			removeDLTKProjectConfig(project);
			break;
		case IModelElementDelta.CHANGED:
			if(delta.getResourceDeltas() == null)
				break;
			for (IResourceDelta resourceDelta : delta.getResourceDeltas()) {
				IResource resource =  resourceDelta.getResource();
				Path cfgpath = new Path(DeeProjectOptions.CFG_FILE_NAME);
				if(resource.getProjectRelativePath().equals(cfgpath)) {
					projectModelLog.println(">> CFG_FILE changed: ", resource);
					removeDLTKProjectConfig(project);
				}
			}
			break;
		default:
			assertFail("!!!! Unknown delta type for project;");
		}
	}
	
	private void addNewDLTKProjectConfig(IScriptProject project) {
		//assertTrue(!deeInfos.containsKey(project));
		if(deeInfos.containsKey(project)) {
			projectModelLog.println("!!!! Warning: adding project that already exists.");
			LangCore.logWarning("Adding project that already exists.");
		}
		
		DmdInstall deeInstall = getInstallForProject(project);
		
		DeeProjectOptions deeProj = DeeProjectOptions.createUsingInstall(project, deeInstall);
		deeInfos.put(project, deeProj);
	}
	
	protected DmdInstall getInstallForProject(IScriptProject project) {
		IInterpreterInstall install = null;
		try {
			install = ScriptRuntime.getInterpreterInstall(project);
		} catch(CoreException e) {
			DeeCore.log(e);
		}
		DmdInstall deeInstall = tryCast(install, DmdInstall.class);
		return deeInstall;
	}
	
	private void removeDLTKProjectConfig(IScriptProject project) {
		deeInfos.remove(project);
	}
	
	public static DeeProjectOptions getDeeProjectInfo(IScriptProject project) {
		assertNotNull(project);
		if(instance.deeInfos.containsKey(project)) {
			DeeProjectOptions info = instance.deeInfos.get(project);
			return info;
		} 
		try {
			return instance.loadProjectInfo(project);
		} catch (CoreException e) {
			projectModelLog.println("Error Loading project config");			
			//throw ExceptionAdapter.unchecked(e);
			return null;
		}
	}
	
	protected DeeProjectOptions loadProjectInfo(IScriptProject project) throws CoreException {
		DmdInstall deeInstall = getInstallForProject(project);
		DeeProjectOptions info = DeeProjectOptions.createUsingInstall(project, deeInstall);
		info.loadNewProjectConfig();
		return info;
	}
	
}