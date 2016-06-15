/*******************************************************************************
 * Copyright (c) 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui.actions;

import static melnorme.utilbox.core.CoreUtil.areEqual;

import java.util.List;
import java.util.function.Function;

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

import dtool.engine.operations.FindDefinitionResult;
import dtool.engine.operations.FindDefinitionResult.FindDefinitionResultEntry;
import melnorme.lang.ide.core.LangCore;
import melnorme.lang.ide.core.utils.EclipseUtils;
import melnorme.lang.ide.ui.EditorSettings_Actual;
import melnorme.lang.ide.ui.editor.EditorUtils;
import melnorme.lang.ide.ui.editor.EditorUtils.OpenNewEditorMode;
import melnorme.lang.ide.ui.utils.UIOperationsStatusHandler;
import melnorme.lang.ide.ui.utils.operations.AbstractEditorOperation2;
import melnorme.lang.tooling.ast.SourceRange;
import melnorme.lang.tooling.common.ops.IOperationMonitor;
import melnorme.utilbox.concurrency.OperationCancellation;
import melnorme.utilbox.core.CommonException;
import melnorme.utilbox.misc.Location;
import melnorme.utilbox.misc.StringUtil;
import melnorme.utilbox.status.Severity;
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
	
	protected IProject getAssociatedProject() {
		return EditorUtils.getAssociatedProject(editor.getEditorInput());
	}
	
	@Override
	protected FindDefinitionResult doBackgroundValueComputation(IOperationMonitor monitor)
			throws CommonException, OperationCancellation {
		IProject associatedProject = getAssociatedProject();
		String dubPath = LangCore.settings().SDK_LOCATION.getValue(associatedProject).toString();
		return DeeEngineClient.getDefault().
				new FindDefinitionOperation(getInputLocation(), offset, -1, dubPath).runEngineOperation(monitor);
	}
	
	@Override
	protected void handleComputationResult(FindDefinitionResult openDefResult) throws CommonException {
		
		List<FindDefinitionResultEntry> results = openDefResult.results;
		
		if(openDefResult.errorMessage != null) {
			statusDialog(Severity.ERROR, openDefResult.errorMessage);
			return;
		}
		
		if(results.size() > 1) {
			statusDialog(Severity.INFO, "Multiple definitions found: \n" 
					+ namedResultsToString(results, "\n") + "\nOpening the first one.");
		}
		
		FindDefinitionResultEntry fdResultEntry = results.get(0);
		
		if(fdResultEntry == null || fdResultEntry.isLanguageIntrinsic()) {
			statusDialog(Severity.INFO, "Cannot open definition, "
					+ "symbol \"" +fdResultEntry.extendedName + "\" is a language intrinsic.");
			return;
		}
		final SourceRange sourceRange = fdResultEntry.sourceRange;
		if(sourceRange == null) {
			String msg = "Symbol " + fdResultEntry.extendedName + " has no source range info!";
			throw new CommonException(msg);
		}
		
		Location newEditorFileLoc = fdResultEntry.modulePath;
		if(newEditorFileLoc == null) {
			throw new CommonException("no file path provided");
		}
		if(!newEditorFileLoc.toFile().exists()) {
			throw new CommonException("File does not exist.");
		}
		
		IEditorInput newInput;
		if(areEqual(newEditorFileLoc, getInputLocation())) {
			newInput = editor.getEditorInput();
		} else {
			newInput = EditorUtils.getBestEditorInputForLoc(newEditorFileLoc);
		}
		
		EclipseUtils.run(() ->
		EditorUtils.openTextEditorAndSetSelection(editor, EditorSettings_Actual.EDITOR_ID, newInput, 
			openNewEditorMode, sourceRange));
	}
	
	protected void statusDialog(Severity severity, String message) {
		UIOperationsStatusHandler.displayStatusMessage(operationName, severity, message);
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