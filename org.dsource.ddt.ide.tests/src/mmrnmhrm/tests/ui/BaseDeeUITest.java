package mmrnmhrm.tests.ui;

import mmrnmhrm.tests.BaseDeeTest;
import mmrnmhrm.ui.DeePlugin;

import org.eclipse.ui.IWorkbenchPage;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;


public class BaseDeeUITest extends BaseDeeTest {
	
	@BeforeClass
	public static void staticTestInit() throws Exception {
		IWorkbenchPage page = DeePlugin.getActivePage();
		page.closeAllEditors(false);
	}
	
	@AfterClass
	public static void staticTestEnd() throws Exception {
	}
	
	@After
	public void after_clearEventQueue() throws Throwable {
		SWTTestUtils.clearEventQueue();
	}
	
	@Override
	public void checkLogErrorListener() throws Throwable {
		SWTTestUtils.clearEventQueue();
		super.checkLogErrorListener();
	}
	
}