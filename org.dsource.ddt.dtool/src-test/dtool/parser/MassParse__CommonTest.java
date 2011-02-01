package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import melnorme.utilbox.core.Function;
import melnorme.utilbox.core.VoidFunction;
import melnorme.utilbox.misc.ArrayUtil;

import org.junit.Test;

import dtool.DeeNamingRules_Test;
import dtool.tests.DToolBaseTest;
import dtool.tests.DToolTestResources;
import dtool.tests.MiscFileUtils;

/**
 * Test conversion of common sources (Phobos, Tango)
 */
public abstract class MassParse__CommonTest extends Parser__CommonTest {
	
	private static final String COMMON_UNPACK = "_common-unpack/";
	
	public static final String TESTSRC_DRUNTIME_PHOBOS2 = "druntime_phobos-2.047-src";
	public static final String TESTSRC_PHOBOS1_OLD = "phobos1-old";
	public static final String TESTSRC_PHOBOS1_OLD__HEADER = TESTSRC_PHOBOS1_OLD + "phobos-header";
	public static final String TESTSRC_PHOBOS1_OLD__INTERNAL = TESTSRC_PHOBOS1_OLD + "phobos-internal";
	public static final String TESTSRC_TANGO_0_99 = "tango-0.99";
	
	static {
		try {
			unzipSource(TESTSRC_DRUNTIME_PHOBOS2);
			unzipSource(TESTSRC_PHOBOS1_OLD);
			unzipSource(TESTSRC_TANGO_0_99);
		} catch(IOException e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
	}
	
	private static void unzipSource(String zipName) throws IOException {
		File zipFile = DToolTestResources.getTestResource(COMMON + zipName + ".zip");
		File outDir = new File(DToolTestResources.getInstance().getWorkingDir(), COMMON_UNPACK + zipName);
		MiscFileUtils.deleteDir(outDir); // Allways delete
		if(!DToolBaseTest.TESTS_LITE_MODE) {
			MiscFileUtils.unzipFile(zipFile, outDir);
		} else {
			outDir.mkdir();
		}
	}
	
	public static File getCommonResource(String subPath) {
		return new File(DToolTestResources.getInstance().getWorkingDir(), COMMON_UNPACK + subPath);
	}
	
	protected static ArrayList<File> getDeeModuleList(File folder, boolean recurseDirs) throws IOException {
		return getDeeModuleList(folder, recurseDirs, false);
	}
	protected static ArrayList<File> getDeeModuleList(File folder, boolean recurseDirs, final boolean validCUsOnly)
			throws IOException {
		
		final boolean addInAnyFileName = !validCUsOnly;
		final ArrayList<File> fileList = new ArrayList<File>();
		
		VoidFunction<File> fileVisitor = new VoidFunction<File>() {
			@Override
			public Void evaluate(File file) {
				if(file.isFile()) {
					fileList.add(file);
				}
				return null;
			}
		};
		
		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File parent, String childName) {
				System.out.println("dir:" + parent +" " + childName);
				File childFile = new File(parent, childName);
				if(childFile.isDirectory()) {
					// exclude team private folder, like .svn, and other crap
					return !childName.startsWith(".");
				} else {
					return addInAnyFileName || DeeNamingRules_Test.isValidCompilationUnitName(childName);
				}
			}
		};
		MiscFileUtils.traverseFiles(folder, recurseDirs, fileVisitor, filter);
		return fileList;
	}
	
	public static Collection<Object[]> getParseFileParameterList(File folder) throws IOException {
		assertTrue(folder.exists() && folder.isDirectory());
		ArrayList<File> deeModuleList = getDeeModuleList(folder, true);
		
		Function<Object, Object[]> arrayWrap = new Function<Object, Object[]>() {
			@Override
			public Object[] evaluate(Object obj) {
				return new Object[] { obj };
			};
		};
		
		return Arrays.asList(ArrayUtil.map(deeModuleList, arrayWrap, Object[].class));
	}
	
	protected static void parseFile(File file, boolean failOnSyntaxErrors, boolean checkSourceRanges) {
		assertTrue(file.isFile());
		String source = readStringFromFileUnchecked(file);
		System.out.println("parsing: " + file);
		testParse(source, failOnSyntaxErrors ? false : null, checkSourceRanges);
	}
	
	/* ------------------------------------ */
	
	protected final File file;
	
	public MassParse__CommonTest(File file) {
		this.file = file;
	}
	
	@Test
	public void testParseFile() throws IOException {
		parseFile(file, failOnSyntaxErrors(), checkSourceRanges());
	}
	
	protected boolean failOnSyntaxErrors() {
		return true;
	}
	
	protected boolean checkSourceRanges() {
		return false;
	}
	
}
