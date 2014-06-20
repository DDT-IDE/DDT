package mmrnmhrm.core.search;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.IScriptProject;

import melnorme.utilbox.core.ExceptionAdapter;
import mmrnmhrm.tests.CommonDeeWorkspaceTest;
import mmrnmhrm.tests.DeeCoreTestResources;

public class SampleSearchProject extends DeeCoreTestResources {
	
	public static final String SAMPLEPROJNAME = "sampleSearchProject";
	
	public static final SampleSearchProject defaultInstance; 
	
	static {
		try {
			defaultInstance = new SampleSearchProject();
		} catch (Exception e) {
			throw ExceptionAdapter.unchecked(e);
		}
	}
	
	
	public final IScriptProject scriptProject;
	
	public SampleSearchProject() throws CoreException {
		scriptProject = CommonDeeWorkspaceTest.createAndOpenDeeProject(SAMPLEPROJNAME);
		fillSampleProj();
	}
	
	protected void fillSampleProj() throws CoreException {
		IProject project = scriptProject.getProject();
		createSrcFolderFromCoreResource(SAMPLEPROJNAME + "/srcA", project.getFolder("srcA"));
		createSrcFolderFromCoreResource(SAMPLEPROJNAME + "/srcB", project.getFolder("srcB"));
	}
	
}
