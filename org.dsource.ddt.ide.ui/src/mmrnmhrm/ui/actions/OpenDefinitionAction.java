package mmrnmhrm.ui.actions;

import mmrnmhrm.lang.ui.AbstractWorkbenchWindowActionDelegate;
import mmrnmhrm.ui.actions.OpenDefinitionOperation.EOpenNewEditor;

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
		OpenDefinitionHandler.executeOperation((ITextEditor) editor, EOpenNewEditor.TRY_REUSING_EXISTING_EDITORS);
	}
	
}