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

import melnorme.lang.ide.ui.actions.AbstractEditorOperation;
import mmrnmhrm.core.engine_client.DToolClient;
import mmrnmhrm.ui.DeeUI;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.internal.ui.editor.EditorUtility;
import org.eclipse.ui.texteditor.ITextEditor;

public abstract class AbstractEditorOperationExt extends AbstractEditorOperation {
	
	protected final ISourceModule sourceModule;
	
	public AbstractEditorOperationExt(String operationName, ITextEditor editor) {
		super(operationName, editor);
		this.sourceModule = EditorUtility.getEditorInputModelElement(editor, false);
	}
	
	@Override
	protected void prepareOperation() throws CoreException {
		super.prepareOperation();
		
		if(sourceModule == null) {
			throw new CoreException(DeeUI.createErrorStatus("No valid editor input in current editor.", null));
		}
	}
	
	@Override
	protected void performLongRunningComputation_do(IProgressMonitor monitor) throws CoreException {
		try {
			DToolClient.getDefault().updateWorkingCopyIfInconsistent(inputPath, doc.get(), sourceModule);
			performLongRunningComputation_withUpdatedServerWorkingCopy();
		} finally {
			if(sourceModule.isWorkingCopy() == false) {
				DToolClient.getDefault().discardServerWorkingCopy(inputPath);
			}
		}
	}
	
	protected abstract void performLongRunningComputation_withUpdatedServerWorkingCopy() throws CoreException;
	
	@Override
	protected abstract void performOperation_handleResult() throws CoreException;
	
}