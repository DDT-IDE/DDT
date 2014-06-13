/*******************************************************************************
 * Copyright (c) 2014, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui.actions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.CoreUtil.areEqual;
import static melnorme.utilbox.core.CoreUtil.tryCast;

import java.nio.file.Path;
import java.util.List;

import melnorme.lang.ide.ui.editor.EditorUtils;
import melnorme.utilbox.core.fntypes.Function;
import melnorme.utilbox.misc.StringUtil;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.projectmodel.DToolClient;
import mmrnmhrm.lang.ui.EditorUtil;
import mmrnmhrm.ui.DeeUI;
import mmrnmhrm.ui.editor.DeeEditor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IReusableEditor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.texteditor.ITextEditor;

import dtool.ast.SourceRange;
import dtool.resolver.api.FindDefinitionResult;
import dtool.resolver.api.FindDefinitionResult.FindDefinitionResultEntry;

public class OpenDefinitionOperation extends AbstractEditorOperation {
	
	protected static final String GO_TO_DEFINITION_OPNAME = "Go to Definition";
	
	public static enum EOpenNewEditor { ALWAYS, TRY_REUSING_EXISTING_EDITORS, NEVER }
	
	protected final EOpenNewEditor openNewEditor;
	protected final int offset;
	
	protected FindDefinitionResult result;
	
	public OpenDefinitionOperation(ITextEditor editor, EOpenNewEditor openNewEditor, int offset) {
		super(GO_TO_DEFINITION_OPNAME, editor);
		this.openNewEditor = openNewEditor;
		this.offset = offset;
	}
	
	public FindDefinitionResult executeSafeWithResult() {
		executeSafe();
		return result;
	}
	
	@Override
	protected FindDefinitionResult doExecute() throws CoreException {
		if(sourceModule == null) {
			throw new CoreException(DeeUI.createErrorStatus("No valid editor input in current editor.", null));
		}
		this.result = DToolClient.doFindDefinition(sourceModule, offset);
		
		assertNotNull(result);
		handleOpenDefinitionResult(result);
		return result;
	}
	
	public void handleOpenDefinitionResult(FindDefinitionResult openDefResult) throws CoreException {
		
		List<FindDefinitionResultEntry> results = openDefResult.results;
		
		if(openDefResult.errorMessage != null) {
			dialogError(window.getShell(), openDefResult.errorMessage);
			return;
		}
		
		if(results.size() > 1) {
			dialogInfo(window.getShell(), "Multiple definitions found: \n" 
					+ namedResultsToString(results, "\n") + "\nOpening the first one.");
		}
		
		FindDefinitionResultEntry fdResultEntry = results.get(0);
		
		if(fdResultEntry == null || fdResultEntry.isLanguageIntrinsic()) {
			// TODO: test this path
			dialogInfo(window.getShell(), 
				"Cannot open editor, element \"" +fdResultEntry.extendedName + "\" is a language intrinsic.");
			return;
		}
		SourceRange sourceRange = fdResultEntry.sourceRange;
		if(sourceRange == null) {
			String msg = "Symbol " +fdResultEntry.extendedName + " has no source range info!";
			handleSystemError(window, msg);
			return;
		}
		
		Path newEditorFilePath = fdResultEntry.compilationUnitPath;
		IEditorInput newInput;
		if(areEqual(newEditorFilePath, openDefResult.originFilePath)) {
			newInput = editor.getEditorInput();
		} else {
			if(newEditorFilePath == null) {
				handleSystemError(window, "no file path provided");
				return;
			}
			newInput = EditorUtils.getBestEditorInputForPath(newEditorFilePath);
		}
		
		IWorkbenchPage page = window.getActivePage();
		openEditor(page, newInput, openNewEditor, editor, sourceRange);
	}
	
	public static String namedResultsToString(Iterable<? extends FindDefinitionResultEntry> nodes, String sep) {
		return StringUtil.iterToString(nodes, sep, new Function<FindDefinitionResultEntry, String>() {
			@Override
			public String evaluate(FindDefinitionResultEntry obj) {
				return obj.extendedName;
			}
		});
	}
	
	public void openEditor(IWorkbenchPage page, IEditorInput newInput, EOpenNewEditor openNewEditor, 
			ITextEditor currentEditor, SourceRange sourceRange) throws CoreException {
		if(sourceRange == null) {
			return;
		}
		
		if(openNewEditor == EOpenNewEditor.NEVER) {
			if(currentEditor.getEditorInput().equals(newInput)) {
				EditorUtil.setEditorSelection(currentEditor, sourceRange);
			} else if(currentEditor instanceof IReusableEditor) {
				IReusableEditor reusableEditor = (IReusableEditor) currentEditor;
				reusableEditor.setInput(newInput);
				EditorUtil.setEditorSelection(currentEditor, sourceRange);
			} else {
				openEditor(page, newInput, EOpenNewEditor.ALWAYS, currentEditor, sourceRange);
			}
		} else {
			int matchFlags = openNewEditor == EOpenNewEditor.ALWAYS ? 
				IWorkbenchPage.MATCH_NONE : IWorkbenchPage.MATCH_INPUT | IWorkbenchPage.MATCH_ID;
			IEditorPart editor = page.openEditor(newInput, DeeEditor.EDITOR_ID, true, matchFlags);
			ITextEditor targetEditor = tryCast(editor, ITextEditor.class);
			if(targetEditor == null) {
				throw new CoreException(DeeCore.createErrorStatus("Not a text editor"));
			}
			EditorUtil.setEditorSelection(targetEditor, sourceRange);
		}
	}
	
	protected void handleSystemError(final IWorkbenchWindow window, String msg) {
		DeeCore.logError(msg);
		dialogError(window.getShell(), msg);
	}
	
	protected void dialogError(Shell shell, String msg) {
		UIUserInteractionsHelper.openError(shell, OpenDefinitionOperation.GO_TO_DEFINITION_OPNAME, msg);
	}
	
	protected void dialogWarning(Shell shell, String msg) {
		UIUserInteractionsHelper.openWarning(shell, OpenDefinitionOperation.GO_TO_DEFINITION_OPNAME, msg);
	}
	
	protected void dialogInfo(Shell shell, String msg) {
		UIUserInteractionsHelper.openInfo(shell, OpenDefinitionOperation.GO_TO_DEFINITION_OPNAME, msg);
	}
	
}