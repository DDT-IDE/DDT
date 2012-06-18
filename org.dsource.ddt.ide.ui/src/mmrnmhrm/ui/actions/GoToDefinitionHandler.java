package mmrnmhrm.ui.actions;

import java.util.Collection;

import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.codeassist.DeeProjectModuleResolver;
import mmrnmhrm.lang.ui.EditorUtil;
import mmrnmhrm.ui.DeePlugin;
import mmrnmhrm.ui.editor.DeeEditor;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.IExternalSourceModule;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.internal.ui.editor.EditorUtility;
import org.eclipse.dltk.internal.ui.editor.ExternalStorageEditorInput;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IReusableEditor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

import dtool.Logg;
import dtool.ast.ASTNeoNode;
import dtool.ast.ASTNodeFinder;
import dtool.ast.ASTPrinter;
import dtool.ast.NodeUtil;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.Module;
import dtool.ast.definitions.Symbol;
import dtool.ast.references.Reference;
import dtool.refmodel.INativeDefUnit;

public class GoToDefinitionHandler extends AbstractHandler  {
	
	public static final String COMMAND_ID = DeePlugin.EXTENSIONS_IDPREFIX+"commands.openDefinition";
	private static final String GO_TO_DEFINITION_OPNAME = "Go to Definition";
	
	public static enum EOpenNewEditor { ALWAYS, TRY_REUSING_EXISTING_EDITORS, NEVER }
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IEditorPart editor = HandlerUtil.getActiveEditorChecked(event);
		try {
			executeOperation((ITextEditor) editor, EOpenNewEditor.TRY_REUSING_EXISTING_EDITORS);
		} catch (CoreException ce) {
			throw new ExecutionException(GO_TO_DEFINITION_OPNAME, ce);
		}
		return null;
	}
	
	
	public static void executeChecked(final ITextEditor srcEditor, final EOpenNewEditor openNewEditor) {
		OperationsManager.executeOperation(new IWorkspaceRunnable() {
			@Override
			public void run(IProgressMonitor monitor) throws CoreException {
				executeOperation(srcEditor, openNewEditor);
			}
		}, GO_TO_DEFINITION_OPNAME);
	}
	
	public static void executeOperation(ITextEditor srcEditor, EOpenNewEditor openNewEditor) throws CoreException {
		TextSelection sel = EditorUtil.getSelection(srcEditor);
		int offset = sel.getOffset();
		
		executeOperation(srcEditor, openNewEditor, offset);
	}
	
	public static void executeOperation(ITextEditor editor, EOpenNewEditor openNewEditor, int offset)
			throws CoreException {
		IWorkbenchWindow window = editor.getSite().getWorkbenchWindow();
		
		Module module = EditorUtil.getModuleFromEditor(editor);
		ASTNeoNode elem = ASTNodeFinder.findElement(module, offset, false);
		
		if(elem == null) {
			dialogWarning(window.getShell(), "No element found at pos: " + offset);
			Logg.main.println(" ! ASTElementFinder null?");
			return;
		}
		Logg.main.println(" Selected Element: " + elem.toStringAsNode(true));
		
		if(elem instanceof Symbol) {
			dialogInfo(window.getShell(),
					"Element is not an entity reference," +" it's already a definition: " + elem.toStringClassName());
			return;
		}
		if(!(elem instanceof Reference)) {
			dialogInfo(window.getShell(),
					"Element is not an entity reference: "+ elem.toStringClassName());
			return;
		}
		
		IModelElement element = EditorUtility.getEditorInputModelElement(editor, false);
		DeeProjectModuleResolver moduleResolver = new DeeProjectModuleResolver(element.getScriptProject());
		
		// find the target
		Collection<DefUnit> defunits = ((Reference)elem).findTargetDefUnits(moduleResolver, false);
		
		if(defunits == null || defunits.size() == 0) {
			dialogWarning(window.getShell(), "Definition not found for entity reference: " + elem.toStringAsElement());
			return;
		}
		
		Logg.main.println(" Find Definition, found: " + ASTPrinter.toStringAsElements(defunits, " ") );
		
		
		if(defunits.size() > 1) {
			dialogInfo(window.getShell(), "Multiple definitions found: \n" 
					+ ASTPrinter.toStringAsElements(defunits, "\n") + "\nGoing to the first one.");
		} 
		
		DefUnit defunit = defunits.iterator().next();
		
		if(defunit.hasNoSourceRangeInfo()) {
			dialogError(window.getShell(), "DefUnit " +defunit.toStringAsElement()+ " has no source range info!");
			return;
		} 
		if(defunit instanceof INativeDefUnit) {
			dialogInfo(window.getShell(), "DefUnit " +defunit.toStringAsElement()+ " is a language native.");
			return;
		} 
		
		
		Module targetModule = NodeUtil.getParentModule(defunit);
		
		if(targetModule == module) {
			IWorkbenchPage page = window.getActivePage();
			openEditor(page, editor.getEditorInput(), openNewEditor, null, defunit);
		} else {
			ISourceModule targetModUnit = moduleResolver.findModuleUnit(targetModule);
			
			IWorkbenchPage page = window.getActivePage();
			
			if (targetModUnit instanceof IExternalSourceModule) {
				IExternalSourceModule externalSourceModule = (IExternalSourceModule) targetModUnit;
				IEditorInput input = new ExternalStorageEditorInput(externalSourceModule);
				openEditor(page, input, openNewEditor, editor, defunit);
			} else if (targetModUnit != null) {
				IFile file = (IFile) DeeCore.getWorkspaceRoot().findMember(targetModUnit.getPath());
				IEditorInput input = new FileEditorInput(file);
				openEditor(page, input, openNewEditor, editor, defunit);
			} else {
				throw new CoreException(DeeCore.createErrorStatus(
						"Don't know how to open editor for: " + targetModUnit));
			}
		}
	}
	
	public static void openEditor(IWorkbenchPage page, IEditorInput input, EOpenNewEditor openNewEditor, 
			ITextEditor editor, DefUnit defunit) throws PartInitException {
		if(openNewEditor == EOpenNewEditor.NEVER) {
			if(editor.getEditorInput().equals(input)) {
				EditorUtil.setEditorSelection(editor, defunit.defname);
			} else if(editor instanceof IReusableEditor) {
				IReusableEditor reusableEditor = (IReusableEditor) editor;
				reusableEditor.setInput(input);
				EditorUtil.setEditorSelection(editor, defunit.defname);
			} else {
				openEditor(page, input, EOpenNewEditor.ALWAYS, editor, defunit);
			}
		} else {
			int matchFlags = openNewEditor == EOpenNewEditor.ALWAYS ? 
				IWorkbenchPage.MATCH_NONE : IWorkbenchPage.MATCH_INPUT | IWorkbenchPage.MATCH_ID;
			ITextEditor targetEditor = (ITextEditor) page.openEditor(input, DeeEditor.EDITOR_ID, true, matchFlags);
			EditorUtil.setEditorSelection(targetEditor, defunit.defname);
		}
	}
	
	
	protected static void dialogError(Shell shell, String msg) {
		OperationsManager.openError(shell, GO_TO_DEFINITION_OPNAME, msg);
	}
	
	protected static void dialogWarning(Shell shell, String msg) {
		OperationsManager.openWarning(shell, GO_TO_DEFINITION_OPNAME, msg);
	}
	
	protected static void dialogInfo(Shell shell, String msg) {
		OperationsManager.openInfo(shell, GO_TO_DEFINITION_OPNAME, msg);
	}
	
}