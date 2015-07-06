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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.texteditor.ITextEditor;

import dtool.ddoc.TextUI;
import melnorme.lang.ide.core.LangCore;
import melnorme.lang.ide.ui.editor.actions.AbstractEditorOperation2;
import melnorme.lang.ide.ui.editor.hover.BrowserControlHover;
import melnorme.lang.ide.ui.editor.hover.ILangEditorTextHover;
import melnorme.utilbox.concurrency.OperationCancellation;
import melnorme.utilbox.core.CommonException;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.engine.DeeEngineClient;

/**
 * Standard documentation hover for DDoc.
 * (used in editor hovers extensions, and editor information provider (F2))
 */
public class DeeDocTextHover extends BrowserControlHover 
		implements ITextHoverExtension, ILangEditorTextHover<String> {
	
	public DeeDocTextHover() {
	}
	
	@Override
	public String getHoverInfo2(ITextViewer textViewer, IRegion hoverRegion) {
		ITextEditor editor = tryCast(getEditor(), ITextEditor.class);
		if(editor == null) {
			return null;
		}
		
		int offset = hoverRegion.getOffset();
		
		String info;
		try {
			try {
				info = new GetDDocHTMLViewOperation("Get DDoc", editor, offset).executeAndGetValidatedResult();
			} catch(CommonException e) {
				throw LangCore.createCoreException(e);
			}
		} catch(CoreException ce) {
			DeeCore.logStatus(ce);
			// TODO: we could add a nicer HTML formatting:
			info = TextUI.convertoToHTML("Error: " + ce.getMessage() + " " + ce.getCause());
		}
		
		if(info != null) {
			return HoverUtil.getCompleteHoverInfo(info, getCSSStyles());
		}
		
		return null;
	}
	
	public static class GetDDocHTMLViewOperation extends AbstractEditorOperation2<String> {
		
		protected final int offset;
		
		public GetDDocHTMLViewOperation(String operationName, ITextEditor editor, int offset) {
			super(operationName, editor);
			this.offset = offset;
		}
		
		@Override
		protected String doBackgroundValueComputation(IProgressMonitor monitor)
				throws CoreException, CommonException, OperationCancellation {
			return DeeEngineClient.getDefault().
					new FindDDocViewOperation(inputLoc, offset, -1).runEngineOperation(monitor);
		}
		
		@Override
		protected void handleComputationResult() throws CoreException {
			// Nothing else to do
		}
		
	}
	
	protected String getCSSStyles() {
		return HoverUtil.getDDocPreparedCSS();
	}
	
}