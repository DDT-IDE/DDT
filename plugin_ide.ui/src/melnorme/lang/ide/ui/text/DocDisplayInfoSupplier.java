/*******************************************************************************
 * Copyright (c) 2016 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.lang.ide.ui.text;

import org.eclipse.core.resources.IProject;

import melnorme.lang.ide.core.LangCore;
import melnorme.lang.ide.core.utils.ResourceUtils;
import melnorme.lang.ide.ui.editor.hover.AbstractDocDisplayInfoSupplier;
import melnorme.lang.tooling.LANG_SPECIFIC;
import melnorme.lang.tooling.ast.SourceRange;
import melnorme.lang.tooling.common.ISourceBuffer;
import melnorme.lang.tooling.common.ops.IOperationMonitor;
import melnorme.lang.tooling.common.ops.ResultOperation;
import melnorme.lang.tooling.toolchain.ops.SourceOpContext;
import melnorme.lang.tooling.toolchain.ops.ToolResponse;
import melnorme.utilbox.concurrency.OperationCancellation;
import melnorme.utilbox.core.CommonException;
import melnorme.utilbox.misc.Location;
import mmrnmhrm.core.engine.DeeEngineClient;
import mmrnmhrm.ui.editor.hover.HoverUtil;

@LANG_SPECIFIC
public class DocDisplayInfoSupplier extends AbstractDocDisplayInfoSupplier {
	
	public DocDisplayInfoSupplier(ISourceBuffer sourceBuffer, int offset) {
		super(sourceBuffer, offset);
	}
	
	@Override
	public String get() {
		String info = super.get();
		
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
	protected ResultOperation<ToolResponse<String>> getOpenDocumentationOperation2(ISourceBuffer sourceBuffer,
			int offset) {
		SourceOpContext opContext = sourceBuffer.getSourceOpContext(new SourceRange(offset, 0));
		
		ResultOperation<ToolResponse<String>> findDocOperation = new ResultOperation<ToolResponse<String>>() {
			
			@Override
			public ToolResponse<String> executeOp(IOperationMonitor om)
					throws CommonException, OperationCancellation {
				return new ToolResponse<>(doGetDoc(om));
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
		
		return findDocOperation;
	}
	
}