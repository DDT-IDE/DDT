package mmrnmhrm.tests;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import melnorme.utilbox.core.ExceptionAdapter;
import melnorme.utilbox.misc.MiscUtil;
import mmrnmhrm.core.dltk.DeeSourceParser;
import mmrnmhrm.core.model.ModelUtil;

import org.dsource.ddt.ide.core.model.DeeModuleDeclaration;
import org.dsource.ddt.ide.core.model.DeeParserUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;

import dtool.parser.MassParse__CommonTest;

/**
 * This class creates the main sample project, in which most tests will be based upon.
 */
public abstract class SampleMainProject extends DeeCoreTestUtils {
	
	
	public static final String SAMPLEPROJNAME = "SampleProj";
	
	public static final String TEST_SRC1 = ITestDataConstants.SAMPLE_SRC1;
	public static final String TEST_SRC3 = ITestDataConstants.SAMPLE_SRC3;
	public static final String TEST_SRC_REFS = "refs";
	public static final String TEST_SRC_CA = "src-ca"; // Content Assist
	public static final String TEST_SRC_OUTSIDE_MODEL = "srcOut"; // Not a source folder
	
	static {
		MiscUtil.loadClass(BaseDeeTest.class);
		SampleMainProject.createAndSetupSampleProj();
	}
	
	public static IProject project;
	public static IScriptProject deeProj;
	
	public static IFile sampleFile1;
	public static IFile sampleOutOfModelFile;
	public static IFile sampleNonExistantFile;
	
	
	public static void createAndSetupSampleProj() {
		try {
			deeProj = BaseDeeTest.createAndOpenProject(SAMPLEPROJNAME);
			fillSampleProj();
		} catch (Exception e) {
			throw ExceptionAdapter.unchecked(e);
		}
	}
	
	protected static void fillSampleProj() throws CoreException, URISyntaxException, IOException {
		// Watch out when changing these values, tests may depend on these paths
		
		project = deeProj.getProject();
		IFolder folder;
		
		sampleNonExistantFile = project.getFile(new Path("nonexistant.d"));
		
		folder = project.getFolder(TEST_SRC1);
		copyDeeCoreDirToWorkspace(ITestDataConstants.SAMPLE_SRC1, folder);
		sampleFile1 = folder.getFile("bigfile.d");
		
		folder = project.getFolder(TEST_SRC_OUTSIDE_MODEL);
		copyDeeCoreDirToWorkspace(TEST_SRC_OUTSIDE_MODEL, folder);
		sampleOutOfModelFile = folder.getFile("outfile.d");
		
		createSrcFolderInProject(TEST_SRC_REFS, project.getFolder(TEST_SRC_REFS));
		
		createSrcFolderInProject(ITestDataConstants.SAMPLE_SRC3, project.getFolder(TEST_SRC3));
		createSrcFolderInProject(TEST_SRC_CA, project.getFolder(TEST_SRC_CA));
		
		
		copyDToolCommonResource(MassParse__CommonTest.TESTSRC_PHOBOS1_OLD);
		ModelUtil.addSourceFolder(project.getFolder(MassParse__CommonTest.TESTSRC_PHOBOS1_OLD__HEADER), null);
		ModelUtil.addSourceFolder(project.getFolder(MassParse__CommonTest.TESTSRC_PHOBOS1_OLD__INTERNAL), null);
		
		copyDToolCommonResource(MassParse__CommonTest.TESTSRC_TANGO_0_99);
		ModelUtil.addSourceFolder(project.getFolder(MassParse__CommonTest.TESTSRC_TANGO_0_99), null);
	}
	
	private static void copyDToolCommonResource(String resourcePath) throws CoreException {
		File testFile = MassParse__CommonTest.getCommonResource(resourcePath);
		copyURLResourceToWorkspace(testFile.toURI(), project.getFolder(resourcePath));
	}
	
	
	/** Gets a IFile from the sample project. */
	public static IFile getFile(String filepath) {
		IFile file = deeProj.getProject().getFile(filepath);
		assertTrue(file.exists(), "Test file not found.");
		return file;
	}
	
	public static ISourceModule getSourceModule(String filepath) {
		return DLTKCore.createSourceModuleFrom(getFile(filepath));
	}
	
	public static DeeModuleDeclaration parsedDeeModule(ISourceModule sourceModule) {
		DeeSourceParser sourceParser = new DeeSourceParser();
		IModuleSource source;
		if (sourceModule instanceof IModuleSource) {
			source = (IModuleSource) sourceModule;
		} else {
			throw assertFail();
		}
		
		DeeModuleDeclaration deeModule = sourceParser.parse(source, null);
		DeeParserUtil.parentizeDeeModuleDeclaration(deeModule, sourceModule);
		return deeModule;
	}
	
}