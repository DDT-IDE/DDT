package mmrnmhrm.core.search;


import org.junit.BeforeClass;
import org.junit.Test;

import melnorme.utilbox.misc.MiscUtil;
import mmrnmhrm.tests.CommonDeeWorkspaceTest;
import mmrnmhrm.tests.SampleMainProject;

/** 
 * Checks for errors that might occur in background thread during DLTKIndexing. 
 */
public class DLTKIndexCheckTest extends CommonDeeWorkspaceTest {
	
	@BeforeClass
	public static void setup() {
		// Load all known projects
		MiscUtil.loadClass(SampleMainProject.class);
		MiscUtil.loadClass(SampleSearchProject.class);
		
		enableDLTKIndexer(true);
		disableDLTKIndexer();
	}
	
	@Test
	public void test() throws Throwable { test$(); }
	public void test$() throws Throwable {
		logErrorListener.checkErrors();
	}
	
}