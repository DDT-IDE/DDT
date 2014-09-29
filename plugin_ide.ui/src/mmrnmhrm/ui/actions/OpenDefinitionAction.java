package mmrnmhrm.ui.actions;

import melnorme.lang.ide.ui.editor.EditorUtils.OpenNewEditorMode;

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;

public class OpenDefinitionAction extends AbstractWorkbenchWindowActionDelegate {
	
	@Override
	public void run(IAction action) {
		if (window == null || window.getActivePage() == null) {
			beep();
			return;
		}
		
		IEditorPart editor = window.getActivePage().getActiveEditor();
		ITextEditor textEditor = (ITextEditor) editor;
		new OpenDefinitionHandler().executeOperation(textEditor, OpenNewEditorMode.TRY_REUSING_EXISTING_EDITORS);
	}
	
}