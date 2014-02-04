package mmrnmhrm.ui.editor;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.lang.ide.ui.WorkbenchUtils;
import mmrnmhrm.tests.ITestResourcesConstants;
import mmrnmhrm.tests.SampleMainProject;
import mmrnmhrm.tests.ui.BaseDeeUITest;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.ide.IDE;
import org.junit.Test;

public class DeeEditor_FoldingTest extends BaseDeeUITest {
	
	// Very basic test, could use more stuff
	
	@Test
	public void testFolding() throws Exception { testFolding$(); }
	public void testFolding$() throws Exception {
		IFile file = SampleMainProject.getFile(ITestResourcesConstants.TR_SAMPLE_SRC1 + "/testFolding.d");
		
		IWorkbenchPage page = WorkbenchUtils.getActivePage();
		IEditorPart editor = IDE.openEditor(page, file, DeeEditor.EDITOR_ID);
		assertTrue(editor instanceof DeeEditor);
	}
	
}
