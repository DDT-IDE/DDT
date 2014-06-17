package mmrnmhrm.lang.ui;




import java.lang.reflect.InvocationTargetException;

import melnorme.lang.ide.ui.utils.ProgressRunnableWithResult;
import mmrnmhrm.core.engine_client.DToolClient;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.internal.ui.editor.EditorUtility;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.ITextEditor;

import dtool.ast.ASTNode;
import dtool.ast.IASTNode;
import dtool.ast.SourceRange;
import dtool.ast.definitions.Module;
import dtool.parser.DeeParserResult.ParsedModule;

public class EditorUtil {
	
	// ------------ used to editor ------------ 
	public static TextSelection getSelection(ITextEditor editor) {
		return (TextSelection) editor.getSelectionProvider().getSelection();
	}
	
	public static void setEditorSelection(ITextEditor textEditor, IASTNode node) {
		setEditorSelection(textEditor, node.getStartPos(), node.getLength());
	}
	
	public static void setEditorSelection(ITextEditor textEditor, SourceRange sourceRange) {
		setEditorSelection(textEditor, sourceRange.getOffset(), sourceRange.getLength()); 
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
	
	public static Module getParsedModule_NoWaitInUI(IModuleSource input) {
		if(Display.getCurrent() == null) {
			return getModuleNode(DToolClient.getDefault().getParsedModuleOrNull(input));
		}
		
		return getModuleNode(DToolClient.getDefault().getExistingParsedModuleOrNull(input));
	}
	
	protected static Module getModuleNode(ParsedModule parsedModule) {
		return parsedModule == null ? null : parsedModule.module;
	}
	
}