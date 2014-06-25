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

import java.nio.file.Path;

import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.engine_client.DToolClient;
import mmrnmhrm.lang.ui.AbstractUIOperation;
import mmrnmhrm.lang.ui.EditorUtil;
import mmrnmhrm.ui.DeeUI;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.internal.ui.editor.EditorUtility;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.texteditor.ITextEditor;

public abstract class AbstractEditorOperation extends AbstractUIOperation {
	
	protected final ITextEditor editor;
	protected final IWorkbenchWindow window;
	protected final IEditorInput editorInput;
	protected final ISourceModule sourceModule;
	protected final Path inputPath;
	protected final IDocument doc;
	
	public AbstractEditorOperation(String operationName, ITextEditor editor) {
		super(operationName);
		this.editor = editor;
		this.window = editor.getSite().getWorkbenchWindow();
		this.editorInput = editor.getEditorInput();
		this.inputPath = EditorUtil.getFilePathFromEditorInput(editorInput);
		this.doc = assertNotNull(editor.getDocumentProvider().getDocument(editor.getEditorInput()));
		this.sourceModule = EditorUtility.getEditorInputModelElement(editor, false);
	}
	
	@Override
	public void executeOperation() throws CoreException {
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
	
	protected abstract void performOperation_do() throws CoreException;
	
	protected void updateWorkingCopyContents() {
		DToolClient.getDefault().updateWorkingCopyIfInconsistent(inputPath, doc.get(), sourceModule);
	}
	
	
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