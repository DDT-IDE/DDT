package mmrnmhrm.tests;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.utilbox.core.ExceptionAdapter;
import mmrnmhrm.core.DeeCore;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.ISourceModule;

/**
 * Builds a simple project with D sources, but that is not a Dee Project
 */
public abstract class SampleNonDeeProject {
	
	
	public static final String SAMPLEPROJNAME = "SampleNonDeeProj";
	
	public static final String TEST_OUT_SRC = ITestResourcesConstants.TR_SRC_OUTSIDE_MODEL;
	
	
	public static IProject project = null;
	
	public static IFile sampleOutOfModelFile;
	public static IFile sampleNonExistantFile;
	
	public static void createAndSetupNonDeeProject() {
		try {
			createAndFillSampleProj();
		} catch (Exception e) {
			throw ExceptionAdapter.unchecked(e);
		}
	}
	
	
	public static IProject createAndFillSampleProj() throws CoreException {
		IWorkspaceRoot workspaceRoot = DeeCore.getWorkspaceRoot();
		project = workspaceRoot.getProject(SAMPLEPROJNAME);
		if(project.exists()) {
			project.delete(true, null);
		}
		project.create(null);
		project.open(null);
		// Watch out when changing these values, tests may depend on these paths
		DeeCoreTestResources.createFolderFromCoreResource(
				ITestResourcesConstants.TR_SAMPLE_SRC1, project.getFolder(TEST_OUT_SRC));
		return project;
	}
	
	
	public static void commonTearDown() throws Exception {
		IWorkspaceRoot workspaceRoot = DeeCore.getWorkspaceRoot();
		IProject project = workspaceRoot.getProject(SAMPLEPROJNAME);
		project.delete(true, null);
	}
	
	/** Gets a IFile from the sample project. */
	public static IFile getFile(String filepath) {
		IFile file = project.getProject().getFile(filepath);
		assertTrue(file.exists(), "Test file not found.");
		return file;
	}
	
	public static ISourceModule getSourceModule(String filepath) {
		return DLTKCore.createSourceModuleFrom(getFile(filepath));
	}
	
}