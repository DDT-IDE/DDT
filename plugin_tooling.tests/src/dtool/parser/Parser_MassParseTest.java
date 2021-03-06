package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import dtool.tests.CommonParameterizedTest;
import dtool.tests.DToolTestResources;
import dtool.tests.DToolTests;
import melnorme.lang.utils.MiscFileUtils;
import java.util.function.Predicate;
import melnorme.utilbox.core.fntypes.VoidFunction;
import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.misc.FileUtil;
import melnorme.utilbox.misc.Location;
import melnorme.utilbox.tests.CommonTest;
import melnorme.utilbox.tests.CommonTestUtils;
import melnorme.utilbox.tests.TestsWorkingDir;

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
		
		FileUtil.deleteDir(getMassParseUnpackedResource()); // Allways delete unpackFolder
		File massParseZipFilesFolder = DToolTestResources.getTestResourceFile(MASSPARSE_ZIPFOLDER);
		for (File zipFile : collectZipFiles(massParseZipFilesFolder)) {
			File unzippedFolder = unzipSource(zipFile);
			testList.add(array(unzippedFolder.getName(), new MassParseTestRunnable(unzippedFolder, zipFile)));
		}
		return testList;
	}
	
	public static ArrayList<File> collectZipFiles(File folder) throws IOException {
		final ArrayList<File> fileList = new ArrayList<>();
		VoidFunction<File> fileVisitor = new VoidFunction<File>() {
			@Override
			public void accept(File file) {
				if(file.isFile() && file.getName().endsWith(".zip")) {
					fileList.add(file);
				}
			}
		};
		MiscFileUtils.traverseFiles(folder, false, fileVisitor);
		return fileList;
	}
	
	protected static File unzipSource(File zipFile) throws IOException {
		File outDir = getMassParseUnpackedResource(zipFile.getName()).toFile();
		if(!DToolTests.TESTS_LITE_MODE) {
			MiscFileUtils.unzipFile(zipFile, outDir, CommonTest.testsLogger);
		} else {
			assertTrue(outDir.mkdirs());
		}
		return outDir;
	}
	
	public static Location getMassParseUnpackedResource(String... segments) {
		Location unpackedRoot = TestsWorkingDir.getWorkingDir().resolve_fromValid(COMMON_UNPACK);
		return Location.fromAbsolutePath(MiscFileUtils.getFile(unpackedRoot.toFile(), segments).toPath());
	}
	
	/* ------------------------------------ */
	
	public Parser_MassParseTest(String testDescription, Runnable testRunnable) {
		super(testDescription, testRunnable);
	}
	
	public static final Pattern LINE_SPLITTER = Pattern.compile("\n|(\r\n)|\r");
	
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
			String exclusionsFileSource = readStringFromFile(exclusionsFiles);
			String[] exclusions = LINE_SPLITTER.split(exclusionsFileSource);
			exclusions = ArrayUtil.filter(exclusions, new Predicate<String>() {
				@Override
				public boolean test(String obj) {
					return obj.trim().isEmpty() == false;
				}
			});
			return exclusions;
		}
		
		public void testFileParse(boolean canHaveSyntaxErrors, File file, String[] exclusions) {
			testsLogVerbose.println("----------> " + DToolTestResources.resourceFileToString(file, COMMON_UNPACK));
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
			
			String source = readStringFromFile_PreserveBOM(file);
			int count = System.getProperty("PerformanceTests") != null ? 5 : 1;
			for (int i = 0; i < count; i++) {
				Boolean expectErrors = canHaveSyntaxErrors ? null : false;
				DeeParsingChecks.runSimpleSourceParseTest(source, "_massParse", expectErrors, false);
			}
		}
		
	}
	
}