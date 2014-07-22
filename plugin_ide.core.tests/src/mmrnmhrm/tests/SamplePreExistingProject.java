package mmrnmhrm.tests;


import melnorme.lang.ide.core.utils.EclipseUtils;
import melnorme.utilbox.core.ExceptionAdapter;
import mmrnmhrm.core.DeeCore;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.dltk.core.IScriptProject;

/**
 * This classes creates a sample project that should exist *before*
 * DeeCore is loaded, with the intent of detecting some startup bugs
 * (this requires that the plugin unit test workspace is not cleared on startup) 
 */
public abstract class SamplePreExistingProject implements ITestResourcesConstants {
	
	
	public static final String PREEXISTINGPROJNAME = "PreExistingDeeProj";
	
	public static final String PROJ_SRC = "src";
	
	private static final boolean REQUIRE_PREEXISTING_PROJ = false;
	
	public static IScriptProject sampleDeeProj = null;
	
	
	public static void checkForExistanceOfPreExistingProject() {
		IWorkspaceRoot workspaceRoot = EclipseUtils.getWorkspaceRoot();
		IProject project = workspaceRoot.getProject(PREEXISTINGPROJNAME);
		
		if(!project.exists()) {
			// If the preexisting project doesn't exist, create it
			try {
				sampleDeeProj = CommonDeeWorkspaceTest.createAndOpenDeeProject(PREEXISTINGPROJNAME);
				project = sampleDeeProj.getProject();
				DeeCoreTestResources.createSrcFolderFromCoreResource(TR_SRC_SIMPLE, project.getFolder(PROJ_SRC));
			} catch (Exception e) {
				DeeCore.logError(e);
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
	
}