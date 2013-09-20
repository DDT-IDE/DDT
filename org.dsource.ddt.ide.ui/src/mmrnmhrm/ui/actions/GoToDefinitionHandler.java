package mmrnmhrm.ui.actions;

import java.util.Collection;
import java.util.Iterator;

import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.codeassist.DeeProjectModuleResolver;
import mmrnmhrm.core.codeassist.ReferenceSwitchHelper;
import mmrnmhrm.lang.ui.EditorUtil;
import mmrnmhrm.ui.DeePlugin;
import mmrnmhrm.ui.editor.DeeEditor;
import mmrnmhrm.ui.views.DeeElementLabelProvider;

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
import org.eclipse.dltk.core.ModelException;
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
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeFinder;
import dtool.ast.definitions.DefSymbol;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.EArcheType;
import dtool.ast.definitions.INamedElement;
import dtool.ast.definitions.Module;
import dtool.ast.references.NamedReference;
import dtool.ast.references.Reference;

public class GoToDefinitionHandler extends AbstractHandler  {
	
	public static final String COMMAND_ID = DeePlugin.EXTENSIONS_IDPREFIX+"commands.openDefinition";
	protected static final String GO_TO_DEFINITION_OPNAME = "Go to Definition";
	
	protected static final String ELEMENT_NEXT_TO_CURSOR_IS_ALREADY_A_DEFINITION_NOT_A_REFERENCE = 
		"Element next to cursor is already a definition, not a reference.";
	protected static final String NO_REFERENCE_FOUND_NEXT_TO_CURSOR = 
		"No reference found next to cursor.";
	protected static final String MISSING_REFERENCE_FOUND_NEXT_TO_CURSOR = 
		"Missing reference found next to cursor.";
	protected static final String NO_NAMED_REFERENCE_FOUND_NEXT_TO_CURSOR = 
		"No named reference found next to cursor.";
	
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
	
	public static void executeOperation(ITextEditor editor, EOpenNewEditor openNewEditor, final int offset)
			throws CoreException {
		final IWorkbenchWindow window = editor.getSite().getWorkbenchWindow();
		
		Module module = EditorUtil.getModuleFromEditor(editor);
		ASTNode node = ASTNodeFinder.findElement(module, offset);
		if(node == null) {
			// Shouldn't happen, apart from threading/concurrency issues
			DeeCore.logError("ASTNodeFinder.findElement(...) == null");
			String msg = "No node found at offset: " + offset;
			dialogError(window.getShell(), msg);
			return;
		}
		
		ReferenceSwitchHelper refPickHelper = new ReferenceSwitchHelper() {
			
			@Override
			protected void nodeIsDefSymbol(DefSymbol defSymbol) {
				dialogInfo(window.getShell(),
					ELEMENT_NEXT_TO_CURSOR_IS_ALREADY_A_DEFINITION_NOT_A_REFERENCE);
			}
			
			@Override
			protected void nodeIsNotReference() {
				dialogInfo(window.getShell(), NO_REFERENCE_FOUND_NEXT_TO_CURSOR);
			}
			
			@Override
			protected void nodeIsNonNamedReference(Reference reference) {
				dialogInfo(window.getShell(), NO_NAMED_REFERENCE_FOUND_NEXT_TO_CURSOR);
			}
			
			@Override
			protected void nodeIsNamedReference_missing(NamedReference namedReference) {
				dialogInfo(window.getShell(), MISSING_REFERENCE_FOUND_NEXT_TO_CURSOR);
			}
			
			@Override
			protected void nodeIsNamedReference_ok(NamedReference namedReference) {
				this.reference = namedReference;
			}
		};
		
		refPickHelper.switchOnPickedNode(node);
		if(refPickHelper.reference == null) {
			return;
		}
		
		findAndOpenTarget(editor, openNewEditor, module, refPickHelper.reference);
	}
	
	public static void findAndOpenTarget(ITextEditor editor, EOpenNewEditor openNewEditor,
		Module module, Reference ref) throws PartInitException, ModelException,
		CoreException {
		
		final IWorkbenchWindow window = editor.getSite().getWorkbenchWindow();
		
		IModelElement element = EditorUtility.getEditorInputModelElement(editor, false);
		DeeProjectModuleResolver moduleResolver = new DeeProjectModuleResolver(element.getScriptProject());
		
		Collection<INamedElement> defElements = ref.findTargetDefElements(moduleResolver, false);
		
		if(defElements == null || defElements.isEmpty()) {
			dialogWarning(window.getShell(), "Definition not found for reference: " + ref.toStringAsCode());
			return;
		}
		
		Logg.main.println(" Find Definition, found: " + collToString_defUnits(defElements, " ") );
		
		
		if(defElements.size() > 1) {
			dialogInfo(window.getShell(), "Multiple definitions found: \n" 
					+ collToString_defUnits(defElements, "\n") + "\nOpening the first one.");
		}
		
		INamedElement defElement = defElements.iterator().next();
		
		if(defElement.isLanguageIntrinsic()) {
			// TODO: test this path
			dialogInfo(window.getShell(), 
				"Cannot open editor, element \"" +defElement.getExtendedName()+ "\" is a language intrinsic.");
			return;
		}
		DefUnit defUnit = defElement.resolveDefUnit();
		if(defUnit == null || !defUnit.hasSourceRangeInfo()) {
			String msg = "DefUnit " +defElement.getExtendedName()+ " has no source range info!";
			dialogError(window.getShell(), msg);
			DeeCore.logError(msg);
			return;
		}
		
		
		Module targetModule = defUnit.getModuleNode();
		
		IWorkbenchPage page = window.getActivePage();
		IEditorInput newInput;
		if(targetModule == module) {
			newInput = editor.getEditorInput();
		} else {
			ISourceModule targetSourceModule = moduleResolver.findModuleUnit(targetModule);
			
			if (targetSourceModule instanceof IExternalSourceModule) {
				IExternalSourceModule externalSourceModule = (IExternalSourceModule) targetSourceModule;
				newInput = new ExternalStorageEditorInput(externalSourceModule);
			} else if (targetSourceModule != null) {
				IFile file = (IFile) DeeCore.getWorkspaceRoot().findMember(targetSourceModule.getPath());
				newInput = new FileEditorInput(file);
			} else {
				throw new CoreException(DeeCore.createErrorStatus(
						"Don't know how to open editor for: " + targetSourceModule));
			}
		}
		openEditor(page, newInput, openNewEditor, editor, defUnit);
	}
	
	public final static String collToString_defUnits(Iterable<? extends INamedElement> nodes, String sep) {
		StringBuilder sb = new StringBuilder();
		Iterator<? extends INamedElement> iter = nodes.iterator();
		for (int i = 0; iter.hasNext(); i++) {
			INamedElement defElement = iter.next();
			if(i > 0) {
				sb.append(sep);
			}
			sb.append(DeeElementLabelProvider.getLabelForContentAssistPopup(defElement));
		}
		return sb.toString();
	}
	
	public static void openEditor(IWorkbenchPage page, IEditorInput newInput, EOpenNewEditor openNewEditor, 
			ITextEditor currentEditor, DefUnit defunit) throws PartInitException {
		if(openNewEditor == EOpenNewEditor.NEVER) {
			if(currentEditor.getEditorInput().equals(newInput)) {
				setSelectionOnDefUnit(currentEditor, defunit);
			} else if(currentEditor instanceof IReusableEditor) {
				IReusableEditor reusableEditor = (IReusableEditor) currentEditor;
				reusableEditor.setInput(newInput);
				setSelectionOnDefUnit(currentEditor, defunit);
			} else {
				openEditor(page, newInput, EOpenNewEditor.ALWAYS, currentEditor, defunit);
			}
		} else {
			int matchFlags = openNewEditor == EOpenNewEditor.ALWAYS ? 
				IWorkbenchPage.MATCH_NONE : IWorkbenchPage.MATCH_INPUT | IWorkbenchPage.MATCH_ID;
			ITextEditor targetEditor = (ITextEditor) page.openEditor(newInput, DeeEditor.EDITOR_ID, true, matchFlags);
			setSelectionOnDefUnit(targetEditor, defunit);
		}
	}
	
	protected static void setSelectionOnDefUnit(ITextEditor editor, DefUnit defunit) {
		if(defunit.defname.hasSourceRangeInfo()) {
			EditorUtil.setEditorSelection(editor, defunit.defname);
		} else {
			if(defunit.getArcheType() == EArcheType.Module) {
				EditorUtil.setEditorSelection(editor, 0, 0);
			}
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