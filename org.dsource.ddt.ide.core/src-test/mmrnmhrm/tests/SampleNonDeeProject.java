package mmrnmhrm.tests;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;

import melnorme.utilbox.core.ExceptionAdapter;
import mmrnmhrm.core.DeeCore;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;

/**
 * Builds a simple project with d sources, but that is not a Dee Project
 */
public abstract class SampleNonDeeProject {
	
	
	public static final String SAMPLEPROJNAME = "SampleNonDeeProj";
	
	public static final String TEST_OUT_SRC = SampleMainProject.TEST_SRC_OUTSIDE_MODEL;
	
	
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
	
	
	public static IProject createAndFillSampleProj() throws CoreException,
			URISyntaxException, IOException {
		IWorkspaceRoot workspaceRoot = DeeCore.getWorkspaceRoot();
		project = workspaceRoot.getProject(SAMPLEPROJNAME);
		if(project.exists()) {
			project.delete(true, null);
		}
		project.create(null);
		project.open(null);
		fillSampleProj();
		return project;
	}
	
	
	public static void fillSampleProj() throws CoreException, URISyntaxException, IOException {
		// Watch out when changing these values, tests may depend on these paths
		DeeCoreTestUtils.copyDeeCoreDirToWorkspace(ITestDataConstants.SAMPLE_SRC1, project.getFolder(TEST_OUT_SRC));
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
	
}