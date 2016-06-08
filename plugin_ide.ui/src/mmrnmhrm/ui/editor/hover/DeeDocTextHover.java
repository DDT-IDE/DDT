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

import java.util.Optional;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.texteditor.ITextEditor;

import dtool.ddoc.TextUI;
import melnorme.lang.ide.core.LangCore;
import melnorme.lang.ide.ui.editor.EditorSourceBuffer;
import melnorme.lang.ide.ui.editor.EditorUtils;
import melnorme.lang.ide.ui.editor.hover.AbstractDocHover;
import melnorme.lang.ide.ui.editor.hover.ILangEditorTextHover;
import melnorme.lang.ide.ui.utils.operations.CalculateValueUIOperation;
import melnorme.lang.tooling.ast.SourceRange;
import melnorme.lang.tooling.common.ISourceBuffer;
import melnorme.lang.tooling.common.ops.IOperationMonitor;
import melnorme.lang.tooling.toolchain.ops.SourceOpContext;
import melnorme.utilbox.concurrency.OperationCancellation;
import melnorme.utilbox.core.CommonException;
import melnorme.utilbox.misc.Location;
import mmrnmhrm.core.engine.DeeEngineClient;

/**
 * Standard documentation hover for DDoc.
 * (used in editor hovers extensions, and editor information provider (F2))
 */
public class DeeDocTextHover extends AbstractDocHover implements ILangEditorTextHover<String> {
	
	public DeeDocTextHover() {
	}
	
	@Override
	protected boolean requiresSavedBuffer() {
		return false;
	}
	
	/* FIXME: refactor this to use common code*/
	@Override
	public String getHoverInfo(ISourceBuffer sourceBuffer, IRegion hoverRegion, Optional<ITextEditor> _editor,
			ITextViewer textViewer, boolean allowedToSaveBuffer) {
		if(!_editor.isPresent()) {
			return null; /* FIXME: */
		}
		ITextEditor editor = _editor.get();
		
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
	
	protected String getCSSStyles() {
		return HoverUtil.getDDocPreparedCSS();
	}
	
	@Override
	protected CalculateValueUIOperation<String> getOpenDocumentationOperation(ISourceBuffer sourceBuffer, int offset) {
		return null;
	}
	
//	@Override
//	protected CalculateValueUIOperation<String> getOpenDocumentationOperation(ITextEditor editor, int offset) {
//		return new GetDDocHTMLViewOperation("Get DDoc", editor, offset);
//	}
	
	public static class GetDDocHTMLViewOperation extends CalculateValueUIOperation<String> {
		
		protected final int offset;
		protected final IProject project;
		protected final SourceOpContext opContext;
		
		public GetDDocHTMLViewOperation(String operationName, ITextEditor editor, int offset) {
			super(operationName, true);
			this.offset = offset;
			
			this.opContext = EditorSourceBuffer.getSourceOpContext(editor, new SourceRange(offset, 0));
			this.project = EditorUtils.getAssociatedProject(editor.getEditorInput());
		}
		
		@Override
		protected String doBackgroundValueComputation(IOperationMonitor monitor)
				throws CommonException, OperationCancellation {
			String dubPath = LangCore.settings().SDK_LOCATION.getValue(project).toString();
			Location fileLocation = opContext.getFileLocation();
			
			return DeeEngineClient.getDefault().
					new FindDDocViewOperation(fileLocation, offset, -1, dubPath).runEngineOperation(monitor);
		}
		
	}
	
}