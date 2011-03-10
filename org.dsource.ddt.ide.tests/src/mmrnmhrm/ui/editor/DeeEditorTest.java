package mmrnmhrm.ui.editor;

import mmrnmhrm.tests.SampleMainProject;
import mmrnmhrm.tests.ui.BaseDeeUITest;
import mmrnmhrm.ui.views.ASTViewer;

import org.dsource.ddt.lang.ui.WorkbenchUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.internal.ui.editor.ScriptEditor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.ide.IDE;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

public class DeeEditorTest extends BaseDeeUITest {

	public static IDocument getDocument(ScriptEditor editor) {
		return editor.getScriptSourceViewer().getDocument();
	}
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void testDeeEditor() throws CoreException {
		IFile file = SampleMainProject.sampleFile1;
		
		IWorkbenchPage page = WorkbenchUtils.getActivePage();
		IEditorPart editor = IDE.openEditor(page, file, DeeEditor.EDITOR_ID);
		assertTrue(editor instanceof DeeEditor, "Assertion failed.");

		page.showView("org.eclipse.ui.views.ContentOutline");
		page.showView(ASTViewer.VIEW_ID);
	}
	
	@Test
	public void testDeeEditor2() throws CoreException {
		IFile file = SampleMainProject.sampleOutOfModelFile;
		
		IWorkbenchPage page = WorkbenchUtils.getActivePage();
		IEditorPart editor = IDE.openEditor(page, file, DeeEditor.EDITOR_ID);
		assertTrue(editor instanceof DeeEditor, "Assertion failed.");

		page.showView("org.eclipse.ui.views.ContentOutline");
		page.showView(ASTViewer.VIEW_ID);
	}
	
	@Test
	public void testDeeEditor3() throws CoreException {
		IWorkbenchPage page = WorkbenchUtils.getActivePage();
		IFile file = SampleMainProject.sampleNonExistantFile;
		//IEditorPart editor = 
			IDE.openEditor(page, file, DeeEditor.EDITOR_ID);
		//UITestUtils.runEventLoop(page.getActivePart().getSite().getShell());
		//assertTrue(!(editor instanceof DeeEditorDLTK));
		//assertTrue(exceptionThrown == true);
		logErrorListener.reset();
	}

}
