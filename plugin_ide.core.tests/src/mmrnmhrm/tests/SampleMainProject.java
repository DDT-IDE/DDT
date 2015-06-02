package mmrnmhrm.tests;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.lang.ide.core.tests.LangCoreTestResources;
import melnorme.utilbox.core.ExceptionAdapter;
import melnorme.utilbox.misc.MiscUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

/**
 * This class creates the main sample project, in which most tests will be based upon.
 */
public abstract class SampleMainProject extends LangCoreTestResources implements ITestResourcesConstants {
	
	public static final String SAMPLEPROJNAME = "SampleProj";
	
	public static IProject project;
	
	public static IFile sampleBigFile;
	public static IFile sampleOutOfModelFile;
	public static IFile sampleNonExistantFile;
	
	static {
		MiscUtil.loadClass(CommonDeeWorkspaceTest.class);
		SampleMainProject.createAndSetupSampleProj();
	}
	
	private static void createAndSetupSampleProj() {
		try {
			project = CommonDeeWorkspaceTest.createAndOpenDeeProject(SAMPLEPROJNAME);
			fillSampleProj();
		} catch (CoreException e) {
			throw ExceptionAdapter.unchecked(e);
		}
	}
	
	protected static void fillSampleProj() throws CoreException {
		// Watch out when changing these values, tests may depend on these paths
		
		IFolder folder;
		
		createFolderFromCoreTestsResource(TR_SAMPLE_SRC1, project.getFolder(TR_SAMPLE_SRC1));
		createFolderFromCoreTestsResource(TR_SAMPLE_SRCBIG, folder = project.getFolder(TR_SAMPLE_SRCBIG));
		sampleBigFile = folder.getFile("bigfile.d");
		
		createFolderFromCoreTestsResource(TR_SAMPLE_SRC3, project.getFolder(TR_SAMPLE_SRC3));
		createFolderFromCoreTestsResource(TR_CA, project.getFolder(TR_CA));
		createFolderFromCoreTestsResource(TR_REFS, project.getFolder(TR_REFS));
		
		createFolderFromCoreTestsResource(TR_SRC_OUTSIDE_MODEL, folder = project.getFolder(TR_SRC_OUTSIDE_MODEL));
		sampleOutOfModelFile = folder.getFile("outfile.d");
		
		CommonDeeWorkspaceTest.writeDubManifest(project, SAMPLEPROJNAME, 
			TR_SAMPLE_SRC1, TR_SAMPLE_SRCBIG, TR_SAMPLE_SRC3, TR_CA, TR_REFS);
		
		sampleNonExistantFile = project.getFile(new Path("nonexistant.d"));
	}
	
	/** Gets a IFile from the sample project. */
	public static IFile getFile(String filepath) {
		IFile file = project.getFile(filepath);
		assertTrue(file.exists(), "Test file not found.");
		return file;
	}
	
}