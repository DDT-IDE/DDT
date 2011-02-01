package dtool.tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import melnorme.utilbox.misc.FileUtil;


public class DToolBaseTest extends DeeTestUtils {
	
	public static final String DTOOL_PREFIX = "DTool.";
	public static final boolean TESTS_LITE_MODE = System.getProperty(DTOOL_PREFIX + "TestsLiteMode") != null;
	
	public static String readTestResourceFile(String filePath) throws IOException {
		File testDataDir = DToolTestResources.getInstance().getResourcesDir();
		File file = new File(testDataDir, filePath);
		return readStringFromFile(file);
	}
	
	private static final String DEFAULT_TESTDATA_ENCODING = "UTF-8";
	
	public static String readStringFromFile(File file) throws IOException, FileNotFoundException {
		return FileUtil.readStringFromFile(file, DEFAULT_TESTDATA_ENCODING);
	}
	
	public static String readStringFromFileUnchecked(File file) {
		try {
			return FileUtil.readStringFromFile(file, DEFAULT_TESTDATA_ENCODING);
		} catch (IOException e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
	}
	
}
