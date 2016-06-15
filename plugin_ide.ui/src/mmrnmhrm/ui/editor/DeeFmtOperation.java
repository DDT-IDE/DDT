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
package mmrnmhrm.ui.editor;

import java.nio.file.Path;

import org.eclipse.ui.texteditor.ITextEditor;

import melnorme.lang.ide.core.DeeToolPreferences;
import melnorme.lang.ide.core.LangCore;
import melnorme.lang.ide.core.operations.ToolManager;
import melnorme.lang.ide.ui.editor.actions.AbstractEditorToolOperation;
import melnorme.lang.tooling.ToolingMessages;
import melnorme.lang.tooling.common.ops.IOperationMonitor;
import melnorme.lang.tooling.toolchain.ops.ToolResponse;
import melnorme.utilbox.collections.ArrayList2;
import melnorme.utilbox.concurrency.OperationCancellation;
import melnorme.utilbox.core.CommonException;
import melnorme.utilbox.misc.StringUtil;
import melnorme.utilbox.process.ExternalProcessHelper.ExternalProcessResult;
import melnorme.utilbox.status.StatusMessage;

public class DeeFmtOperation extends AbstractEditorToolOperation<String> {
	
	protected final ToolManager toolMgr = LangCore.getToolManager();
	
	public DeeFmtOperation(ITextEditor editor) {
		super("Format", editor);
	}
	
	@Override
	protected ToolResponse<String> doBackgroundValueComputation(IOperationMonitor monitor)
			throws CommonException, OperationCancellation {
		
		Path rustFmt = DeeToolPreferences.DFMT_PATH.getDerivedValue(project);
		
		ArrayList2<String> cmdLine = new ArrayList2<>(rustFmt.toString());
		
		ProcessBuilder pb = new ProcessBuilder(cmdLine);
		// set directory, for fmt to look for the config file in folders parent chain
		pb.directory(getInputLocation().getParent().toFile());
		
		String input = doc.get();
		ExternalProcessResult result = toolMgr.runEngineTool(pb, input, monitor);
		int exitValue = result.exitValue;
		
		if(exitValue != 0) {
			String stdErr = result.getStdErrBytes().toUtf8String();
			String firstStderrLine = StringUtil.splitString(stdErr, '\n')[0].trim();
			
			String errorMessage = ToolingMessages.PROCESS_CompletedWithNonZeroValue("dfmt", exitValue) + "\n" +
					firstStderrLine;
			return new ToolResponse<>(null, new StatusMessage(errorMessage));
		}
		
		// formatted file is in stdout
		return new ToolResponse<>(result.getStdOutBytes().toUtf8String());
	}
	
	@Override
	protected void handleResultData(String resultData) throws CommonException {
		if(resultData != null) {
			setEditorTextPreservingCarret(resultData);
		}
	}
	
}