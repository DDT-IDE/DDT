package mmrnmhrm.lang.ui;




import java.nio.file.Path;

import melnorme.lang.ide.ui.editor.EditorUtils;
import mmrnmhrm.core.engine_client.DToolClient;

import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import dtool.ast.ASTNode;
import dtool.ast.definitions.Module;
import dtool.parser.DeeParserResult.ParsedModule;

public class EditorUtil {
	
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
				EditorUtils.setEditorSelection(editor, start, end);
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
	
}