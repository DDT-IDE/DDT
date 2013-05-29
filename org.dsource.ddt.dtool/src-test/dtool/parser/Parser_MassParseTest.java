package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import dtool.tests.DToolBaseTest;
import dtool.tests.DToolTestResources;
import dtool.tests.DToolTests;
import dtool.tests.MiscFileUtils;

/**
 * Test conversion of common sources (Phobos, Tango)
 */
@RunWith(Parameterized.class)
public class Parser_MassParseTest extends CommonParameterizedTest {
	
	public static final String MASSPARSE_ZIPFOLDER = "parser-massParse";
	// zip filename marker to indicate that files can have syntax errors:
	protected static final String BAD_SYNTAX = "#BAD_SYNTAX"; 
	
	protected static final String COMMON_UNPACK = "_common-unpack";
	
	@Parameters(name="{index}: {0}")
	public static Collection<Object[]> testParameters() throws IOException {
		return collectTestParameters();
	}
	
	public static Collection<Object[]> collectTestParameters() throws IOException {
		final Collection<Object[]> testList = new ArrayList<>();
		
		MiscFileUtils.deleteDir(getMassParseUnpackedResource()); // Allways delete unpackFolder
		File massParseZipFilesFolder = DToolTestResources.getTestResource(MASSPARSE_ZIPFOLDER);
		for (File zipFile : MiscFileUtils.collectZipFiles(massParseZipFilesFolder)) {
			File unzippedFolder = unzipSource(zipFile);
			addMassParseTest(testList, unzippedFolder);
		}
		return testList;
	}
	
	protected static File unzipSource(File zipFile) throws IOException {
		File outDir = getMassParseUnpackedResource(zipFile.getName());
		if(!DToolTests.TESTS_LITE_MODE) {
			MiscFileUtils.unzipFile(zipFile, outDir);
		} else {
			assertTrue(outDir.mkdirs());
		}
		return outDir;
	}
	
	public static File getMassParseUnpackedResource(String... segments) {
		return getFile(getFile(DToolTestResources.getWorkingDir(), COMMON_UNPACK), segments);
	}
	
	public static void addMassParseTest(final Collection<Object[]> testList, File unpackedFolder) {
		testList.add(new Object[] { unpackedFolder.getName(), new MassParseTestRunnable(unpackedFolder) });
	}
	
	/* ------------------------------------ */
	
	public Parser_MassParseTest(String testDescription, Runnable testRunnable) {
		super(testDescription, testRunnable);
	}
	
	public static class MassParseTestRunnable extends DToolBaseTest implements Runnable {
		
		public File unpackedFolder;
		
		public MassParseTestRunnable(File unpackedFolder) {
			this.unpackedFolder = unpackedFolder;
		}
		
		@Override
		public void run() {
			ArrayList<File> moduleList = getDeeModuleList(unpackedFolder);
			boolean canHaveSyntaxErrors = unpackedFolder.getName().contains(BAD_SYNTAX);
			for (File file : moduleList) {
				testFileParse(canHaveSyntaxErrors, file);
			}
		}
		
		public void testFileParse(boolean canHaveSyntaxErrors, File file) {
			testsLogger.println("----------> " + DToolTestResources.resourceFileToString(file, COMMON_UNPACK));
			String source = readStringFromFileUnchecked(file);
			Parser__CommonTest.parseSource(source, null, false, "_tests_unnamed_");
//			Parser__CommonTest.testParseSource(source, canHaveSyntaxErrors ? null : false, false, "_unnamed");
		}
		
	}
	
}