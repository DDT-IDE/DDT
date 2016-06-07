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

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.texteditor.ITextEditor;

import dtool.ddoc.TextUI;
import melnorme.lang.ide.core.LangCore;
import melnorme.lang.ide.ui.editor.EditorUtils;
import melnorme.lang.ide.ui.editor.hover.BrowserControlHover;
import melnorme.lang.ide.ui.editor.hover.ILangEditorTextHover;
import melnorme.lang.ide.ui.utils.operations.AbstractEditorOperation2;
import melnorme.lang.tooling.common.ops.IOperationMonitor;
import melnorme.lang.tooling.common.ops.IOperationMonitor.NullOperationMonitor;
import melnorme.utilbox.concurrency.OperationCancellation;
import melnorme.utilbox.core.CommonException;
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
	public String getHoverInfo(ITextEditor editor, ITextViewer textViewer, IRegion hoverRegion) {
		if(editor == null) {
			return null;
		}
		
		int offset = hoverRegion.getOffset();
		
		String info;
		try {
			GetDDocHTMLViewOperation op = new GetDDocHTMLViewOperation("Get DDoc", editor, offset);
			info = op.executeAndGetValidatedResult();
		} catch(CommonException ce) {
			LangCore.logStatusException(ce.toStatusException());
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
		protected final IProject project;
		
		public GetDDocHTMLViewOperation(String operationName, ITextEditor editor, int offset) {
			super(operationName, editor);
			this.offset = offset;
			
			this.project = EditorUtils.getAssociatedProject(editor.getEditorInput());
		}
		
		@Override
		public String executeAndGetValidatedResult() throws CommonException {
//			assertTrue(Display.getCurrent() != null);
			
			execute();
			return getResultValue();
		}
		
		@Override
		protected void doOperation() throws CommonException, OperationCancellation {
			if(Display.getCurrent() == null) {
				// Perform computation directly in this thread, cancellation won't be possible.
				runBackgroundComputation(new NullOperationMonitor());
				return;
			}
			super.doOperation();
		}
		
		@Override
		protected String doBackgroundValueComputation(IOperationMonitor monitor)
				throws CommonException, OperationCancellation {
			String dubPath = LangCore.settings().SDK_LOCATION.getValue(project).toString();
			return DeeEngineClient.getDefault().
					new FindDDocViewOperation(getInputLocation(), offset, -1, dubPath).runEngineOperation(monitor);
		}
		
	}
	
	protected String getCSSStyles() {
		return HoverUtil.getDDocPreparedCSS();
	}
	
}