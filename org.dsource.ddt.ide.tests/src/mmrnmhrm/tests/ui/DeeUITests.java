package mmrnmhrm.tests.ui;

import java.io.IOException;

import dtool.tests.DToolBaseTest;

public class DeeUITests {
	
	public static String readResource(String path) {
		try {
			return DToolBaseTest.readTestResourceFile(path);
		} catch (IOException e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
	}
	
}
