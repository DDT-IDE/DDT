package mmrnmhrm.ui.editor.ref;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.utilbox.misc.ReflectionUtils;
import mmrnmhrm.tests.SampleMainProject;
import mmrnmhrm.tests.ui.BaseDeeUITest;
import mmrnmhrm.tests.ui.SWTTestUtils;
import mmrnmhrm.ui.editor.DeeEditor;

import org.dsource.ddt.lang.ui.WorkbenchUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.dltk.internal.ui.editor.ScriptEditor;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.contentassist.ContentAssistEvent;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.ICompletionListener;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.junit.Test;

public class ContentAssist_InteractionTest extends BaseDeeUITest {
	
	protected final IFile file;
	protected final ScriptEditor editor;
	
	public ContentAssist_InteractionTest() {
		this.file = SampleMainProject.getFile("src-ca/testCodeCompletion.d");
		this.editor = openDeeEditorForFile(file);
	}

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
	
	
	@Test
	public void testMoveCursorBeforeStartOffset() throws Exception { testMoveCursorBeforeStartOffset$(); }
	public void testMoveCursorBeforeStartOffset$() throws Exception {
		String source = editor.getScriptSourceViewer().getDocument().get();
		int ccOffset = source.indexOf("/+CC.I@+/");
		assertTrue(ccOffset >= 0);
		ISourceViewer viewer = editor.getViewer();
		viewer.setSelectedRange(ccOffset, 0);
		assertTrue(viewer.getSelectedRange().x == ccOffset);
		
		
		ContentAssistant ca = getContentAssistant(editor);
		CompletionWatcher caWatcher = new CompletionWatcher();
		ca.addCompletionListener(caWatcher);
		
		viewer.revealRange(ccOffset, 10);
		invokeContentAssist();
		checkCAWatcherActive(caWatcher, true);
		
		simulateCursorLeft(); // at start of defunit
		SWTTestUtils.________________flushUIEventQueue________________();
		assertTrue(viewer.getSelectedRange().x == ccOffset - 1);
		checkCAWatcherActive(caWatcher, true);
		
		
		simulateCursorLeft(); // before defunit
		SWTTestUtils.________________flushUIEventQueue________________();
		assertTrue(viewer.getSelectedRange().x == ccOffset - 2);
		
		checkCAWatcherActive(caWatcher, false); // Assert content Assist closed
	}
	
	protected void checkCAWatcherActive(CompletionWatcher caWatcher, boolean expected) {
//		if(caWatcher.active != expected) {
//			Display display = Display.getCurrent(); 
//			while (editor.getViewer() != null && !editor.getViewer().getTextWidget().isDisposed()) {
//				if (!display.readAndDispatch ()) display.sleep ();
//			}
//		}
		assertTrue(caWatcher.active == expected);
	}
	
	private void simulateCursorLeft() {
		// TODO: we should use SWTbot instead
		Event event = new Event();
		event.character = 0;
		event.keyCode = SWT.ARROW_LEFT;
		event.type = SWT.KeyDown;
		editor.getViewer().getTextWidget().notifyListeners(SWT.KeyDown, event);
		event.type = SWT.KeyUp;
		editor.getViewer().getTextWidget().notifyListeners(SWT.KeyUp, event);
	}
	
	protected void invokeContentAssist() {
		ITextOperationTarget target = (ITextOperationTarget) editor.getAdapter(ITextOperationTarget.class);
		if(target != null && target.canDoOperation(ISourceViewer.CONTENTASSIST_PROPOSALS)) {
			target.doOperation(ISourceViewer.CONTENTASSIST_PROPOSALS);
		}
	}
	
	protected class CompletionWatcher implements ICompletionListener {
		protected boolean active = false;
		protected ContentAssistEvent lastEvent;
		@Override
		public void selectionChanged(ICompletionProposal proposal, boolean smartToggle) {
		}
		
		@Override
		public void assistSessionStarted(ContentAssistEvent event) {
			active = true;
			lastEvent = event;
		}
		
		@Override
		public void assistSessionEnded(ContentAssistEvent event) {
			active = false;
		}
	}
	
	public static ContentAssistant getContentAssistant(ScriptEditor scriptEditor) {
		// Need to do this because AdaptedSourceViewer is not extendable
		Object caField = ReflectionUtils.readField(scriptEditor.getScriptSourceViewer(), "fContentAssistant");
		ContentAssistant ca = (ContentAssistant) caField;
		return ca;
	}
	
}