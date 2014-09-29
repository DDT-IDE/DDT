package mmrnmhrm.ui.actions;

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
		new DeeOpenDefinitionHandler().runOperation(textEditor);
	}
	
}