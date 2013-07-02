package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import melnorme.utilbox.core.Predicate;
import melnorme.utilbox.misc.ArrayUtil;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import dtool.tests.CommonTestUtils;
import dtool.tests.DToolTestResources;
import dtool.tests.DToolTests;
import dtool.tests.MiscDeeTestUtils;
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
			testList.add(array(unzippedFolder.getName(), new MassParseTestRunnable(unzippedFolder, zipFile)));
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
	
	/* ------------------------------------ */
	
	public Parser_MassParseTest(String testDescription, Runnable testRunnable) {
		super(testDescription, testRunnable);
	}
	
	public static class MassParseTestRunnable extends CommonTestUtils implements Runnable {
		
		public final File unpackedFolder;
		public final File zipFile;
		
		public MassParseTestRunnable(File unpackedFolder, File zipFile) {
			this.unpackedFolder = unpackedFolder;
			this.zipFile = zipFile;
		}
		
		@Override
		public void run() {
			ArrayList<File> moduleList = getDeeModuleList(unpackedFolder);
			boolean canHaveSyntaxErrors = unpackedFolder.getName().contains(BAD_SYNTAX);
			String[] exclusions = getExclusions();
			
			for (File file : moduleList) {
				testFileParse(canHaveSyntaxErrors, file, exclusions);
			}
		}
		
		public String[] getExclusions() {
			File exclusionsFiles = new File(zipFile.toString() + ".EXCLUSIONS");
			if(!exclusionsFiles.exists()) 
				return new String[0];
			String exclusionsFileSource = readStringFromFileUnchecked(exclusionsFiles);
			String[] exclusions = MiscDeeTestUtils.splitLines(exclusionsFileSource);
			exclusions = ArrayUtil.filter(exclusions, new Predicate<String>() {
				@Override
				public boolean evaluate(String obj) {
					return obj.trim().isEmpty() == false;
				}
			});
			return exclusions;
		}
		
		public void testFileParse(boolean canHaveSyntaxErrors, File file, String[] exclusions) {
			testsLogger.println("----------> " + DToolTestResources.resourceFileToString(file, COMMON_UNPACK));
			String fileName = file.getName().toLowerCase();
			if(!(fileName.endsWith(".d") || fileName.endsWith(".di")) ) {
				canHaveSyntaxErrors = true;
			}
			if(canHaveSyntaxErrors == false) {
				for (String exclusion : exclusions) {
					String uriString = file.toURI().toString();
					if(uriString.endsWith(exclusion)) {
						canHaveSyntaxErrors = true;
						break;
					}
				}
			}
			
			String source = readStringFromFileUnchecked(file);
			int count = System.getProperty("PerformanceTests") != null ? 5 : 1;
			for (int i = 0; i < count; i++) {
				DeeParsingChecks.runSimpleSourceParseTest(source, "_massParse", canHaveSyntaxErrors ? null : false, false);
			}
		}
		
	}
	
}