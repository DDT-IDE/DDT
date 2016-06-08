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

import melnorme.lang.ide.core.LangCore;
import melnorme.lang.ide.core.utils.ResourceUtils;
import melnorme.lang.ide.ui.editor.hover.AbstractDocHover;
import melnorme.lang.ide.ui.editor.hover.ILangEditorTextHover;
import melnorme.lang.tooling.ast.SourceRange;
import melnorme.lang.tooling.common.ISourceBuffer;
import melnorme.lang.tooling.common.ops.CommonOperation;
import melnorme.lang.tooling.common.ops.IOperationMonitor;
import melnorme.lang.tooling.toolchain.ops.SourceOpContext;
import melnorme.lang.tooling.toolchain.ops.ToolOpResult;
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
	
	@Override
	public String getHoverInfo(ISourceBuffer sourceBuffer, IRegion hoverRegion, Optional<ITextEditor> _editor,
			ITextViewer textViewer, boolean allowedToSaveBuffer) {
		
		String info = super.getHoverInfo(sourceBuffer, hoverRegion, _editor, textViewer, allowedToSaveBuffer);
		
		if(info != null) {
			return HoverUtil.getCompleteHoverInfo(info, getCSSStyles());
		}
		return null;
	}
	
	protected String getCSSStyles() {
		return HoverUtil.getDDocPreparedCSS();
	}
	
	@Override
	protected String escapeToHTML(String rawDocumentation) {
		// don't escape, DDoc has HTML already
		return rawDocumentation;
	}
	
	@Override
	protected OpenDocumentationOperation getOpenDocumentationOperation(ISourceBuffer sourceBuffer, int offset) {
		
		SourceOpContext opContext = sourceBuffer.getSourceOpContext(new SourceRange(offset, 0));
		
		CommonOperation<ToolOpResult<String>> findDocOperation = new CommonOperation<ToolOpResult<String>>() {

			@Override
			public ToolOpResult<String> executeOp(IOperationMonitor om)
					throws CommonException, OperationCancellation {
				return new ToolOpResult<String>(doGetDoc(om));
			}
			
			protected String doGetDoc(IOperationMonitor monitor) throws CommonException, OperationCancellation {
				IProject project = ResourceUtils.getProject(sourceBuffer.getLocation_opt());
				String dubPath = LangCore.settings().SDK_LOCATION.getValue(project).toString();
				Location fileLocation = opContext.getFileLocation();
				
				int offset = opContext.getOffset();
				return DeeEngineClient.getDefault().
						new FindDDocViewOperation(fileLocation, offset, -1, dubPath).runEngineOperation(monitor);
			}
		};
		
		return new OpenDocumentationOperation("Get DDoc", findDocOperation);
	}
	
}