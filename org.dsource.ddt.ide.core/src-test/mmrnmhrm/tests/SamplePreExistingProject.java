package mmrnmhrm.tests;


import java.io.IOException;
import java.net.URISyntaxException;

import melnorme.utilbox.core.ExceptionAdapter;
import mmrnmhrm.core.DeeCore;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.IScriptProject;

/**
 * This classes creates a sample project that should exist *before*
 * DeeCore is loaded, with the intent of detecting some startup bugs
 * (this requires that the plugin unit test workspace is not cleared 
 *  on startup) 
 */
public abstract class SamplePreExistingProject {


	public static final String PREEXISTINGPROJNAME = "ExistingProj";

	public static final String TEST_SRC1 = "src1";

	private static final boolean REQUIRE_PREEXISTING_PROJ = false;
	
	public static IScriptProject sampleDeeProj = null;
	public static IProject project;
	

	public static void checkForExistanceOfPreExistingProject() {
		IWorkspaceRoot workspaceRoot = DeeCore.getWorkspaceRoot();
		IProject project = workspaceRoot.getProject(PREEXISTINGPROJNAME);

		if(!project.exists()) {
			// If the preexisting project doesn't exist, create it
			try {
				sampleDeeProj = BaseDeeTest.createAndOpenProject(PREEXISTINGPROJNAME);
				fillPreExistingSampleProj();
			} catch (Exception e) {
				ExceptionAdapter.unchecked(e);
			}
			// And throw up, to force restarting the unit tests
			if(REQUIRE_PREEXISTING_PROJ)
			throw new RuntimeException("The pre-existing project was not found,"
					+ "and was now created. Please restart the plugin unit tests"
					+ "and make the workspace is not cleared.");
		}
				
		return;
	}

	private static void fillPreExistingSampleProj() throws CoreException, URISyntaxException, IOException {
		project = sampleDeeProj.getProject();
		DeeCoreTestUtils.createSrcFolderInProject("sampleSrc1", project.getFolder(TEST_SRC1));
	}

}