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

import java.nio.file.Path;
import java.util.List;

import melnorme.lang.ide.ui.editor.EditorUtils;
import melnorme.lang.ide.ui.editor.EditorUtils.OpenNewEditorMode;
import melnorme.lang.tooling.ast.SourceRange;
import melnorme.utilbox.core.fntypes.Function;
import melnorme.utilbox.misc.StringUtil;
import mmrnmhrm.core.engine_client.DToolClient;
import mmrnmhrm.ui.editor.DeeEditor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

import dtool.engine.operations.FindDefinitionResult;
import dtool.engine.operations.FindDefinitionResult.FindDefinitionResultEntry;

public class OpenDefinitionOperation extends AbstractEditorOperation {
	
	protected static final String OPEN_DEFINITION_OPNAME = "Open Definition";
	
	protected final OpenNewEditorMode openNewEditorMode;
	protected final int offset;
	
	protected FindDefinitionResult findDefResult;
	
	public OpenDefinitionOperation(ITextEditor editor, OpenNewEditorMode openNewEditorMode, int offset) {
		super(OPEN_DEFINITION_OPNAME, editor);
		this.openNewEditorMode = openNewEditorMode;
		this.offset = offset;
	}
	
	public FindDefinitionResult executeWithResult() {
		executeHandled();
		return findDefResult;
	}
	
	@Override
	protected void performLongRunningComputation_withUpdatedServerWorkingCopy() {
		findDefResult = DToolClient.getDefault().doFindDefinition(inputPath, offset);
	}
	
	@Override
	protected void performOperation_handleResult() throws CoreException {
		assertNotNull(findDefResult);
		handleOpenDefinitionResult(findDefResult);
	}
	
	public void handleOpenDefinitionResult(FindDefinitionResult openDefResult) throws CoreException {
		
		List<FindDefinitionResultEntry> results = openDefResult.results;
		
		if(openDefResult.errorMessage != null) {
			dialogError(openDefResult.errorMessage);
			return;
		}
		
		if(results.size() > 1) {
			dialogInfo("Multiple definitions found: \n" 
					+ namedResultsToString(results, "\n") + "\nOpening the first one.");
		}
		
		FindDefinitionResultEntry fdResultEntry = results.get(0);
		
		if(fdResultEntry == null || fdResultEntry.isLanguageIntrinsic()) {
			dialogInfo("Cannot open definition, "
					+ "symbol \"" +fdResultEntry.extendedName + "\" is a language intrinsic.");
			return;
		}
		SourceRange sourceRange = fdResultEntry.sourceRange;
		if(sourceRange == null) {
			String msg = "Symbol " + fdResultEntry.extendedName + " has no source range info!";
			handleSystemError(msg);
			return;
		}
		
		Path newEditorFilePath = fdResultEntry.modulePath;
		IEditorInput newInput;
		if(areEqual(newEditorFilePath, inputPath)) {
			newInput = editor.getEditorInput();
		} else {
			if(newEditorFilePath == null) {
				handleSystemError("no file path provided");
				return;
			}
			newInput = EditorUtils.getBestEditorInputForPath(newEditorFilePath);
		}
		
		EditorUtils.openEditor(editor, DeeEditor.EDITOR_ID, newInput, sourceRange, openNewEditorMode);
	}
	
	public static String namedResultsToString(Iterable<? extends FindDefinitionResultEntry> nodes, String sep) {
		return StringUtil.iterToString(nodes, sep, new Function<FindDefinitionResultEntry, String>() {
			@Override
			public String evaluate(FindDefinitionResultEntry obj) {
				return obj.extendedName;
			}
		});
	}
	
}