package mmrnmhrm.lang.ui;




import mmrnmhrm.core.parser.DeeModuleParsingUtil;

import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.internal.ui.editor.EditorUtility;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.ITextEditor;

import dtool.ast.ASTNode;
import dtool.ast.IASTNode;
import dtool.ast.definitions.Module;

public class EditorUtil {
	
	// ------------ used to editor ------------ 
	public static TextSelection getSelection(ITextEditor editor) {
		return (TextSelection) editor.getSelectionProvider().getSelection();
	}
	
	public static void setEditorSelection(ITextEditor textEditor, IASTNode node) {
		setEditorSelection(textEditor, node.getStartPos(), node.getLength());
	}
	
	public static void setEditorSelection(ITextEditor textEditor, int offset, int length) {
		textEditor.getSelectionProvider().setSelection(new TextSelection(offset, length)); 
	}
	
	public static IEditorPart getActiveEditor() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			IWorkbenchPage page = window.getActivePage();
			if (page != null) {
				return page.getActiveEditor();
			}
		}
		return null;
	}

	public static void selectNodeInEditor(AbstractTextEditor editor, SelectionChangedEvent event) {
		ISelection selection = event.getSelection();
		if (selection.isEmpty())
			editor.resetHighlightRange();
		else {
			IStructuredSelection sel = (IStructuredSelection) selection;
			ASTNode node = (ASTNode) sel.getFirstElement();
			
			if(!node.hasSourceRangeInfo())
				return;
			
			int start = node.getOffset();
			int end = node.getLength();
			try {
				editor.setHighlightRange(start, end, true);
				setEditorSelection(editor, start, end);
			} catch (IllegalArgumentException x) {
				editor.resetHighlightRange();
			}
		}
	}
	
	
	// ------------  Used by editor ------------ 
	
	public static ISourceModule getModuleUnit(IEditorPart textEditor) {
		IModelElement element = EditorUtility.getEditorInputModelElement(textEditor, false);
		if(!(element instanceof ISourceModule))
			return null;
		return (ISourceModule) element;
	}
	
	/** Gets a Module from this editor's input, and setups the Module'
	 * modUnit as the editor's input. */
	public static Module getModuleFromEditor(IEditorPart textEditor) {
		IModelElement element = EditorUtility.getEditorInputModelElement(textEditor, false);
		if(!(element instanceof ISourceModule))
			return null;
		ISourceModule modUnit = (ISourceModule) element;
		Module module = DeeModuleParsingUtil.getParsedDeeModule(modUnit);
		return module == null ? null : module;
	}
	
}