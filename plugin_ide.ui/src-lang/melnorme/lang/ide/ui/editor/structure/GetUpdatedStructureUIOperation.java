/*******************************************************************************
 * Copyright (c) 2015, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.lang.ide.ui.editor.structure;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import melnorme.lang.ide.core.LangCore;
import melnorme.lang.ide.core.engine.SourceModelManager;
import melnorme.lang.ide.core.engine.SourceModelManager.StructureInfo;
import melnorme.lang.ide.ui.utils.operations.CalculateValueUIOperation;
import melnorme.lang.tooling.common.ops.IOperationMonitor;
import melnorme.lang.tooling.structure.SourceFileStructure;
import melnorme.lang.tooling.structure.StructureElement;
import melnorme.utilbox.concurrency.OperationCancellation;
import melnorme.utilbox.core.CommonException;
import melnorme.utilbox.core.fntypes.Result;

public class GetUpdatedStructureUIOperation extends CalculateValueUIOperation<SourceFileStructure> {
	
	protected final SourceModelManager modelManager;
	protected final AbstractLangStructureEditor editor;
	
	public GetUpdatedStructureUIOperation(AbstractLangStructureEditor editor) {
		this(LangCore.getSourceModelManager(), editor);
	}
	
	public GetUpdatedStructureUIOperation(SourceModelManager modelManager, AbstractLangStructureEditor editor) {
		super("Awaiting Structure Calculation");
		this.modelManager = assertNotNull(modelManager);
		this.editor = assertNotNull(editor);
	}
	
	@Override
	protected String getTaskName() {
		return operationName;
	}
	
	@Override
	protected void prepareOperation() throws CommonException {
		if(editor.modelRegistration == null) {
			throw new CommonException("Editor not connected to a structure model.", null);
		}
		structureInfo = editor.modelRegistration.structureInfo;
	}
	
	protected StructureInfo structureInfo;
	
	@Override
	protected boolean isBackgroundComputationNecessary() throws CommonException {
		if(structureInfo.isStale()) {
			return true;
		} else {
			Result<SourceFileStructure, CommonException> structureResult = structureInfo.getStoredData();
			if(structureResult.isException()) {
				// TODO: retry computation if in error
			}
			result = structureResult.get();
			return false;
		}
	}
	
	@Override
	protected SourceFileStructure doBackgroundValueComputation(IOperationMonitor om) 
			throws OperationCancellation, CommonException {
		return structureInfo.awaitUpdatedData(om).get();
	}
	
	@Override
	protected void handleComputationResult() throws CommonException {
		if(result == null) {
			throw new CommonException(
				"Could not retrieve source file structure for: " + structureInfo.getKey2().getLabel());
		}
	}
	
	/* ----------------- util ----------------- */
	
	public static StructureElement getUpdatedStructureElementAt(AbstractLangStructureEditor editor, int offset) {
		GetUpdatedStructureUIOperation op = new GetUpdatedStructureUIOperation(editor);
		SourceFileStructure sourceFileStructure = op.executeAndGetHandledResult();
		
		if(sourceFileStructure == null) {
			return null; // Note, possible error result has already been handled and reported to the user.
		}
		return sourceFileStructure.getStructureElementAt(offset);
	}
	
}