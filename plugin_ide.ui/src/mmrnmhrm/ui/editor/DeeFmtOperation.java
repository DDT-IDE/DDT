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

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.texteditor.ITextEditor;

import melnorme.lang.ide.core.DeeToolPreferences;
import melnorme.lang.ide.core.LangCore;
import melnorme.lang.ide.core.operations.ToolManager;
import melnorme.lang.ide.ui.editor.EditorUtils;
import melnorme.lang.ide.ui.utils.operations.AbstractEditorOperation2;
import melnorme.lang.tooling.ToolingMessages;
import melnorme.lang.tooling.ops.IOperationMonitor;
import melnorme.utilbox.collections.ArrayList2;
import melnorme.utilbox.concurrency.OperationCancellation;
import melnorme.utilbox.core.CommonException;
import melnorme.utilbox.misc.StringUtil;
import melnorme.utilbox.process.ExternalProcessHelper.ExternalProcessResult;

public class DeeFmtOperation extends AbstractEditorOperation2<String> {
	
	protected final ToolManager toolMgr = LangCore.getToolManager();
	
	protected boolean fmtFailureAsHardFailure = true;
	
	public DeeFmtOperation(ITextEditor editor) {
		super("Format", editor);
	}
	
	@Override
	protected String doBackgroundValueComputation(IOperationMonitor monitor)
			throws CommonException, OperationCancellation {
		
		IProject project = EditorUtils.getAssociatedProject(editorInput);
		
		Path rustFmt = DeeToolPreferences.DFMT_PATH.getDerivedValue(project);
		
		ArrayList2<String> cmdLine = new ArrayList2<>(rustFmt.toString());
		
		ProcessBuilder pb = new ProcessBuilder(cmdLine);
		// set directory, for fmt to look for the config file in folders parent chain
		pb.directory(inputLoc.getParent().toFile());
		
		String input = doc.get();
		ExternalProcessResult result = toolMgr.runEngineTool(pb, input, monitor);
		int exitValue = result.exitValue;
		
		if(exitValue != 0) {
			String stdErr = result.getStdErrBytes().toUtf8String();
			String firstStderrLine = StringUtil.splitString(stdErr, '\n')[0].trim();
			
			statusErrorMessage = ToolingMessages.PROCESS_CompletedWithNonZeroValue("dfmt", exitValue) + "\n" +
					firstStderrLine;
			return null;
		}
		
		// formatted file is in stdout
		return result.getStdOutBytes().toUtf8String();
	}
	
	@Override
	protected void handleStatusErrorMessage(String statusErrorMessage) throws CommonException {
		if(fmtFailureAsHardFailure) {
			throw new CommonException(statusErrorMessage);
		} else {
			super.handleStatusErrorMessage(statusErrorMessage);
		}
	}
	
	@Override
	protected void handleComputationResult() throws CommonException {
		super.handleComputationResult();
		
		if(result != null) {
			setEditorTextPreservingCarret(result);
		}
	}
	
}