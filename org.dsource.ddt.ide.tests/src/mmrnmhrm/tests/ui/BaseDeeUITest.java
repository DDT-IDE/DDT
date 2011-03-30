package mmrnmhrm.tests.ui;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import mmrnmhrm.tests.BaseDeeTest;

import org.dsource.ddt.lang.ui.WorkbenchUtils;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.intro.IIntroPart;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;


public class BaseDeeUITest extends BaseDeeTest {
	
	@BeforeClass
	public static void staticTestInit() throws Exception {
		IWorkbenchPage page = WorkbenchUtils.getActivePage();
		page.closeAllEditors(false);
		
		IIntroPart intro = PlatformUI.getWorkbench().getIntroManager().getIntro();
		PlatformUI.getWorkbench().getIntroManager().closeIntro(intro);
		SWTTestUtils.________________flushUIEventQueue________________();
	}
	
	@AfterClass
	public static void staticTestEnd() throws Exception {
	}
	
	
	@Before
	public void checkWorbench() throws Exception {
		assertTrue(PlatformUI.getWorkbench().getIntroManager().getIntro() == null);
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