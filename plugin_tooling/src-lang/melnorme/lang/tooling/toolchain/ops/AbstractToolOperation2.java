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
package melnorme.lang.tooling.toolchain.ops;

import melnorme.lang.tooling.ToolingMessages;
import melnorme.lang.utils.parse.StringCharSource;
import melnorme.utilbox.concurrency.OperationCancellation;
import melnorme.utilbox.core.CommonException;
import melnorme.utilbox.misc.StringUtil;
import melnorme.utilbox.process.ExternalProcessHelper.ExternalProcessResult;

public abstract class AbstractToolOperation2<RESULT> implements ToolOutputParseHelper {
	
	public AbstractToolOperation2() {
		super();
	}
	
	public RESULT parseResult(ExternalProcessResult result) throws CommonException, OperationCancellation {
		return handleProcessResult(result).get();
	}
	
	public ToolOpResult<RESULT> handleProcessResult(ExternalProcessResult result) 
			throws CommonException, OperationCancellation {
		validateExitCode(result);
		
		return doHandleProcessResult(result);
	}
	
	protected void validateExitCode(ExternalProcessResult result) throws CommonException {
		if(result.exitValue != 0) {
			throw new CommonException(
			ToolingMessages.PROCESS_CompletedWithNonZeroValue(getToolProcessName(), result.exitValue));
		}
	}
	
	protected abstract String getToolProcessName();
	
	protected ToolOpResult<RESULT> doHandleProcessResult(ExternalProcessResult result) 
			throws CommonException {
		return parseProcessOutput(result.getStdOutBytes().toString(StringUtil.UTF8));
	}
	
	public final ToolOpResult<RESULT> parseProcessOutput(String source) 
			throws CommonException {
		return parseProcessOutput(new StringCharSource(source));
	}
	
	public abstract ToolOpResult<RESULT> parseProcessOutput(StringCharSource outputParseSource) throws CommonException;
	
}