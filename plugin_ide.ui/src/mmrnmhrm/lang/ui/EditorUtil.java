package mmrnmhrm.lang.ui;




import java.nio.file.Path;
import java.nio.file.Paths;

import melnorme.lang.tooling.ast.SourceRange;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.engine_client.DToolClient;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.ITextEditor;

import dtool.ast.ASTNode;
import dtool.ast.IASTNode;
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
	
	
	// ------------  syntax and semantic operations util ------------ 
	
	public static Module getParsedModule_NoWaitInUI(IModuleSource input) {
		Path filePath = DToolClient.getPathHandleForModuleSource(input);
		if(filePath == null) {
			return null;
		}
		
		if(Display.getCurrent() == null) {
			return getModuleNode(DToolClient.getDefaultModuleCache().getParsedModuleOrNull(filePath, input));
		}
		
		return getModuleNode(DToolClient.getDefaultModuleCache().getExistingParsedModule(filePath));
	}
	
	protected static Module getModuleNode(ParsedModule parsedModule) {
		return parsedModule == null ? null : parsedModule.module;
	}
	
	public static Path getFilePathFromEditorInput(IEditorInput editorInput) {
		IURIEditorInput uriEditorInput;
		if(editorInput instanceof IURIEditorInput) {
			uriEditorInput = (IURIEditorInput) editorInput;
		} else {
			uriEditorInput = (IURIEditorInput) editorInput.getAdapter(IURIEditorInput.class);
		}
		if(uriEditorInput != null) {
			try {
				return Paths.get(uriEditorInput.getURI());
			} catch (Exception e) {
			}
		}
		if(editorInput instanceof IStorageEditorInput) {
			IStorageEditorInput storageEditorInput = (IStorageEditorInput) editorInput;
			try {
				IPath fullPath = storageEditorInput.getStorage().getFullPath();
				if(fullPath != null) {
					return fullPath.toFile().toPath();
				}
			} catch (CoreException e) {
				DeeCore.logError(e);
			}
		}
		
		return null;
	}
	
}