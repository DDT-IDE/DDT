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

import java.util.List;
import java.util.function.Function;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

import dtool.engine.operations.FindDefinitionResult;
import dtool.engine.operations.FindDefinitionResult.FindDefinitionResultEntry;
import melnorme.lang.ide.core.LangCore;
import melnorme.lang.ide.ui.EditorSettings_Actual;
import melnorme.lang.ide.ui.editor.EditorUtils;
import melnorme.lang.ide.ui.editor.EditorUtils.OpenNewEditorMode;
import melnorme.lang.ide.ui.editor.actions.AbstractEditorOperation2;
import melnorme.lang.tooling.ast.SourceRange;
import melnorme.utilbox.concurrency.OperationCancellation;
import melnorme.utilbox.core.CommonException;
import melnorme.utilbox.misc.Location;
import melnorme.utilbox.misc.StringUtil;
import mmrnmhrm.core.engine.DeeEngineClient;

public class DeeOpenDefinitionOperation extends AbstractEditorOperation2<FindDefinitionResult> {
	
	protected static final String OPEN_DEFINITION_OPNAME = "Open Definition";
	
	protected final OpenNewEditorMode openNewEditorMode;
	protected final int offset;
	
	public DeeOpenDefinitionOperation(ITextEditor editor) {
		this(editor, OpenNewEditorMode.TRY_REUSING_EXISTING);
	}
	
	public DeeOpenDefinitionOperation(ITextEditor editor, OpenNewEditorMode openNewEditorMode) {
		this(editor, openNewEditorMode, EditorUtils.getSelectionSR(editor).getOffset());
	}
	
	public DeeOpenDefinitionOperation(ITextEditor editor, OpenNewEditorMode openNewEditorMode, int offset) {
		super(OPEN_DEFINITION_OPNAME, editor);
		this.openNewEditorMode = openNewEditorMode;
		this.offset = offset;
	}
	
	@Override
	protected FindDefinitionResult doBackgroundValueComputation(IProgressMonitor monitor)
			throws CoreException, CommonException, OperationCancellation {
		return DeeEngineClient.getDefault().
				new FindDefinitionOperation(inputLoc, offset, -1).runEngineOperation(monitor);
	}
	
	@Override
	protected void handleComputationResult() throws CoreException {
		assertNotNull(result);
		handleOpenDefinitionResult(result);
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
		final SourceRange sourceRange = fdResultEntry.sourceRange;
		if(sourceRange == null) {
			String msg = "Symbol " + fdResultEntry.extendedName + " has no source range info!";
			throw LangCore.createCoreException(msg, null);
		}
		
		Location newEditorFileLoc = fdResultEntry.modulePath;
		if(newEditorFileLoc == null) {
			throw LangCore.createCoreException("no file path provided", null);
		}
		if(!newEditorFileLoc.toFile().exists()) {
			throw LangCore.createCoreException("File does not exist.", null);
		}
		
		IEditorInput newInput;
		if(areEqual(newEditorFileLoc, inputLoc)) {
			newInput = editor.getEditorInput();
		} else {
			newInput = EditorUtils.getBestEditorInputForLoc(newEditorFileLoc);
		}
		
		EditorUtils.openTextEditorAndSetSelection(editor, EditorSettings_Actual.EDITOR_ID, newInput, 
			openNewEditorMode, sourceRange);
	}
	
	public static String namedResultsToString(Iterable<? extends FindDefinitionResultEntry> nodes, String sep) {
		return StringUtil.toString(nodes, sep, new Function<FindDefinitionResultEntry, String>() {
			@Override
			public String apply(FindDefinitionResultEntry obj) {
				return obj.extendedName;
			}
		});
	}
	
}