package mmrnmhrm.ui.actions;

import java.util.Collection;

import mmrnmhrm.core.DeeCore;
import mmrnmhrm.lang.ui.EditorUtil;
import mmrnmhrm.ui.DeePlugin;
import mmrnmhrm.ui.DeePluginImages;
import mmrnmhrm.ui.editor.DeeEditor;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.IExternalSourceModule;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.internal.ui.editor.ExternalStorageEditorInput;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.ide.IDE;
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
	public static final ImageDescriptor IMAGE_DESC = 
			DeePluginImages.getActionImageDescriptor("gotodef.gif", true);
	private static final String GO_TO_DEFINITION_OPNAME = "Go to Definition";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		IEditorPart editor = HandlerUtil.getActiveEditorChecked(event);
		try {
			executeOperation((ITextEditor) editor, false);
		} catch (CoreException ce) {
			throw new ExecutionException(GO_TO_DEFINITION_OPNAME, ce);
		}
		return null;
	}



	public static void executeChecked(final ITextEditor srcEditor,
			final boolean openNewEditor) {
		OperationsManager.executeOperation(new IWorkspaceRunnable() {
			@Override
			public void run(IProgressMonitor monitor) throws CoreException {
				executeOperation(srcEditor, openNewEditor);
			}
		}, GO_TO_DEFINITION_OPNAME);
	}

	public static void executeOperation(ITextEditor srcEditor,
			boolean openNewEditor) throws CoreException {

		TextSelection sel = EditorUtil.getSelection(srcEditor);
		int offset = sel.getOffset();
		
		executeOperation(srcEditor, openNewEditor, offset);

	}

	public static void executeOperation(ITextEditor editor,
			boolean openNewEditor, int offset) throws CoreException {
		IWorkbenchWindow window = editor.getSite().getWorkbenchWindow();

		Module neoModule = EditorUtil.getNeoModuleFromEditor(editor);

		ASTNeoNode elem = ASTNodeFinder.findElement(neoModule, offset, false);
		
		if(elem == null) {
			dialogWarning(window.getShell(), "No element found at pos: " + offset);
			Logg.main.println(" ! ASTElementFinder null?");
			return;
		}
		Logg.main.println(" Selected Element: " + elem.toStringAsNode(true));

		if(elem instanceof Symbol) {
			dialogInfo(window.getShell(),
					"Element is not an entity reference,"
					+" it's already a definition: " + elem.toStringClassName());
			return;
		}
		if(!(elem instanceof Reference)) {
			dialogInfo(window.getShell(),
					"Element is not an entity reference: "+ elem.toStringClassName());
			return;
		} 
		
		// find the target
		Collection<DefUnit> defunits = ((Reference)elem).findTargetDefUnits(false);
		
		if(defunits == null || defunits.size() == 0) {
			dialogWarning(window.getShell(), 
					"Definition not found for entity reference: " 
					+ elem.toStringAsElement());
			return;
		}

		Logg.main.println(" Find Definition, found: " 
				+ ASTPrinter.toStringAsElements(defunits, " ") );
		
		
		if(defunits.size() > 1) {
			dialogInfo(window.getShell(), 
					"Multiple definitions found: \n" 
					+ ASTPrinter.toStringAsElements(defunits, "\n")
					+ "\nGoing to the first one.");
		} 

		DefUnit defunit = defunits.iterator().next();
		
		if(defunit.hasNoSourceRangeInfo()) {
			dialogError(window.getShell(), "DefUnit " 
					+defunit.toStringAsElement()+ " has no source range info!");
			return;
		} 
		if(defunit instanceof INativeDefUnit) {
			dialogInfo(window.getShell(),
				"DefUnit " +defunit.toStringAsElement()+ " is a language native.");
			return;
		} 
		
		ITextEditor targetEditor;

		Module targetModule = NodeUtil.getParentModule(defunit);

		ISourceModule modUnit = (ISourceModule) targetModule.getModuleUnit();

		if(openNewEditor || neoModule != targetModule) {
			IWorkbenchPage page = window.getActivePage();
			// getCorrespondingResource isn't with linked folders 
			//IFile file = (IFile) modUnit.getCorrespondingResource();
			String editorID = DeeEditor.EDITOR_ID;
			if (modUnit instanceof IExternalSourceModule) {
				targetEditor = (ITextEditor) IDE.openEditor(page, new ExternalStorageEditorInput(
						(IStorage) modUnit), editorID);
			} else if (modUnit instanceof ISourceModule) {
				IFile file = (IFile) DeeCore.getWorkspaceRoot().findMember(modUnit.getPath());
				targetEditor = (ITextEditor) IDE.openEditor(page, file, editorID);
			} else {
				throw new CoreException(DeeCore.createErrorStatus(
						"Don't know how to open editor for: " + modUnit));
			}
		} else {
			targetEditor = editor;
		}
		EditorUtil.setSelection(targetEditor, defunit.defname);
	}
	

	private static void dialogError(Shell shell, String msg) {
		OperationsManager.openError(shell,
				GO_TO_DEFINITION_OPNAME, msg);
	}

	static void dialogWarning(Shell shell, String msg) {
		OperationsManager.openWarning(shell,
				GO_TO_DEFINITION_OPNAME, msg);
	}

	static void dialogInfo(Shell shell, String msg) {
		OperationsManager.openInfo(shell,
				GO_TO_DEFINITION_OPNAME, msg);
	}
	
	
}
