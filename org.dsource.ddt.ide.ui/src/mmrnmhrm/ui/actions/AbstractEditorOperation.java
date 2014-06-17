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

import mmrnmhrm.core.DeeCore;
import mmrnmhrm.lang.ui.AbstractUIOperation;
import mmrnmhrm.ui.DeeUI;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.internal.ui.editor.EditorUtility;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.texteditor.ITextEditor;

public abstract class AbstractEditorOperation extends AbstractUIOperation {
	
	protected final ITextEditor editor;
	protected final ISourceModule sourceModule;
	protected final IWorkbenchWindow window;
	
	public AbstractEditorOperation(String operationName, ITextEditor editor) {
		super(operationName);
		this.editor = editor;
		this.window = editor.getSite().getWorkbenchWindow();
		this.sourceModule = EditorUtility.getEditorInputModelElement(editor, false);
	}
	
	
	@Override
	protected void performOperation() throws CoreException {
		if(sourceModule == null) {
			throw new CoreException(DeeUI.createErrorStatus("No valid editor input in current editor.", null));
		}
		
		try {
			performLongRunningComputation();
		} catch (InterruptedException e) {
			return;
		}
		
		performOperation_do();
	}
	
	protected void performOperation_do() throws CoreException { }
	
	
	protected void dialogError(String msg) {
		UIUserInteractionsHelper.openError(window.getShell(), operationName, msg);
	}
	
	protected void dialogWarning(String msg) {
		UIUserInteractionsHelper.openWarning(window.getShell(), operationName, msg);
	}
	
	protected void dialogInfo(String msg) {
		UIUserInteractionsHelper.openInfo(window.getShell(), operationName, msg);
	}
	
	protected void handleSystemError(String msg) {
		DeeCore.logError(msg);
		dialogError(msg);
	}
	
}