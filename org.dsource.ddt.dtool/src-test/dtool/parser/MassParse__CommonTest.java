package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.File;
import java.io.IOException;

import dtool.tests.DToolTestResources;
import dtool.tests.DToolTests;
import dtool.tests.MiscFileUtils;

/**
 * Test conversion of common sources (Phobos, Tango)
 */
public abstract class MassParse__CommonTest extends Parser__FileParseTest {
	
	private static final String COMMON_UNPACK = "_common-unpack/";
	
	public static final String TESTSRC_DRUNTIME_PHOBOS2 = "druntime_phobos-2.047-src";
	public static final String TESTSRC_PHOBOS1_OLD = "phobos1-old";
	public static final String TESTSRC_TANGO_0_99 = "tango-0.99";
	
	public static final String TESTSRC_PHOBOS1_OLD__HEADER = TESTSRC_PHOBOS1_OLD + "/phobos-header";
	public static final String TESTSRC_PHOBOS1_OLD__INTERNAL = TESTSRC_PHOBOS1_OLD + "/phobos-internal";
	
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
		File outDir = new File(DToolTestResources.getWorkingDir(), COMMON_UNPACK + zipName);
		MiscFileUtils.deleteDir(outDir); // Allways delete
		if(!DToolTests.TESTS_LITE_MODE) {
			MiscFileUtils.unzipFile(zipFile, outDir);
		} else {
			assertTrue(outDir.mkdirs());
		}
	}
	
	public static File getCommonResource(String subPath) {
		return new File(DToolTestResources.getWorkingDir(), COMMON_UNPACK + subPath);
	}
	
	/* ------------------------------------ */
	
	public MassParse__CommonTest(File file) {
		super(file);
	}
	
}
