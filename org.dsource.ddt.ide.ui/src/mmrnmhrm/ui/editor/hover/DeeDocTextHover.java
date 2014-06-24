/*******************************************************************************
 * Copyright (c) 2008, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/

package mmrnmhrm.ui.editor.hover;

import static melnorme.utilbox.core.CoreUtil.tryCast;
import melnorme.lang.ide.ui.editor.BestMatchHover;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.engine_client.DToolClient;
import mmrnmhrm.ui.actions.AbstractEditorOperation;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.internal.ui.editor.EditorUtility;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.texteditor.ITextEditor;

import dtool.ddoc.TextUI;

/**
 * Standard documentation hover for DDoc.
 * (used in editor hovers extensions, and editor information provider (F2))
 */
public class DeeDocTextHover extends AbstractDocTextHover {
	
	public DeeDocTextHover() {
	}
	
	@Override
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		return BestMatchHover.doGetHoverRegion(textViewer, offset);
	}
	
	@Override
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		ITextEditor editor = tryCast(getEditor(), ITextEditor.class);
		if(editor == null) {
			return null;
		}
		ISourceModule sourceModule = EditorUtility.getEditorInputModelElement(editor, false);
		if(sourceModule == null) {
			return null;
		}
		
		int offset = hoverRegion.getOffset();
		
		GetDDocViewOperation ddocOp = new GetDDocViewOperation("Get DDoc", editor, offset);
		try {
			ddocOp.executeOperation();
		} catch (CoreException e) {
			DeeCore.logError(e);
			return TextUI.convertoToHTML("Error: " + e.getMessage() + " " + e.getCause());					
		}
		String info = ddocOp.info;
		
		if(info != null) {
			return HoverUtil.getCompleteHoverInfo(info, getCSSStyles());
		}
		
		return null;
	}
	
	public static class GetDDocViewOperation extends AbstractEditorOperation {
		
		protected final int offset;
		protected String info;
		
		public GetDDocViewOperation(String operationName, ITextEditor editor, int offset) {
			super(operationName, editor);
			this.offset = offset;
		}
		
		@Override
		protected void performLongRunningComputation_do() {
			info = DToolClient.getDefault().getDDocHTMLView(inputPath, offset);
		}
		
		@Override
		protected void performOperation_do() throws CoreException {
			// Nothing else to do
		}
		
	}

}