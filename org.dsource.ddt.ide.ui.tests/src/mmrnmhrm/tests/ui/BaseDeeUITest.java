package mmrnmhrm.tests.ui;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import mmrnmhrm.tests.BaseDeeTest;
import mmrnmhrm.ui.editor.DeeEditor;
import mmrnmhrm.ui.views.DeePerspective;

import org.dsource.ddt.lang.ui.WorkbenchUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.dltk.internal.ui.editor.ScriptEditor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.intro.IIntroPart;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;


public class BaseDeeUITest extends BaseDeeTest {
	
	static {
		assertTrue(PlatformUI.getWorkbench() != null);
		IWorkbenchPage page = WorkbenchUtils.getActivePage();
		page.closeAllEditors(false);
		
		IIntroPart intro = PlatformUI.getWorkbench().getIntroManager().getIntro();
		PlatformUI.getWorkbench().getIntroManager().closeIntro(intro);
		
		page.setPerspective(
			PlatformUI.getWorkbench().getPerspectiveRegistry().findPerspectiveWithId(DeePerspective.PERSPECTIVE_ID));
		
		SWTTestUtils.________________flushUIEventQueue________________();
	}
	
	@AfterClass
	public static void staticTestEnd() throws Exception {
		WorkbenchUtils.getActivePage().closeAllEditors(false);
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
	
	/*--------------*/
	
	public static ScriptEditor openDeeEditorForFile(IFile file) {
		IWorkbenchPage page = WorkbenchUtils.getActivePage();
		try {
			ScriptEditor editor = (ScriptEditor) IDE.openEditor(page, file, DeeEditor.EDITOR_ID);
			assertTrue(editor.getScriptSourceViewer() != null);
			return editor;
		} catch(PartInitException e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
	}
	
}