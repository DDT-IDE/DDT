package mmrnmhrm.core.projectmodel;


import java.util.HashMap;
import java.util.Map;

import melnorme.utilbox.core.ExceptionAdapter;
import mmrnmhrm.core.LangCore;

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

import dtool.Logg;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

/**
 * The D Model. 
 * It is a listener for the DLTK Model, and maintains a list of DeeProjects.
 */
public class DeeProjectModel implements IElementChangedListener {
	
	private static DeeProjectModel instance;
	
	public static void initModel() {
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
			Logg.nolog.println("delta received: ");
			Logg.nolog.println(delta);
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
			Logg.main.println(">> Adding project: ", project);
			addNewDLTKProject(project);
			break;
		case IModelElementDelta.REMOVED:
			Logg.main.println(">> Removing project: ", project);
			removeDLTKProject(project);
			break;
		case IModelElementDelta.CHANGED:
			if(delta.getResourceDeltas() == null)
				break;
			for (IResourceDelta resourceDelta : delta.getResourceDeltas()) {
		    	IResource resource =  resourceDelta.getResource();
		    	Path cfgpath = new Path(DeeProjectOptions.CFG_FILE_NAME);
				if(resource.getProjectRelativePath().equals(cfgpath)) {
					Logg.main.println(">> CFG_FILE changed: ", resource);
					removeDLTKProject(project);
				}
			}
			break;
		default:
			assertFail("!!!! Unknown delta type for project;");
		}
	}

	private void removeDLTKProject(IScriptProject project) {
		//assertTrue(deeInfos.containsKey(project));
		deeInfos.remove(project);
	}


	private void addNewDLTKProject(IScriptProject project) {
		//assertTrue(!deeInfos.containsKey(project));
		if(deeInfos.containsKey(project)) {
			Logg.main.println("!!!! Warning: adding project that already exists.");
			LangCore.logWarning("Adding project that already exists.");
		}
		DeeProjectOptions deeProj = new DeeProjectOptions(project);
		deeInfos.put(project, deeProj);
	}
	
	
	public static DeeProjectOptions getDeeProjectInfo(IScriptProject project) {
		assertNotNull(project);
		if(instance.deeInfos.containsKey(project)) {
			DeeProjectOptions info = instance.deeInfos.get(project);
			return info;
		} 
		try {
			return loadProjectInfo(project);
		} catch (CoreException e) {
			// TODO: maybe check this
			ExceptionAdapter.unchecked(e);
			return null;
		}
	}


	private static DeeProjectOptions loadProjectInfo(IScriptProject project) throws CoreException {
		DeeProjectOptions info = new DeeProjectOptions(project);
		info.loadNewProjectConfig();
		return info;
	}


}