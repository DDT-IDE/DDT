/*******************************************************************************
 * Copyright (c) 2014, 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.core.engine;

import melnorme.lang.ide.core.CoreSettings;
import melnorme.lang.ide.core.operations.ToolManager;
import melnorme.lang.ide.core.utils.CoreExecutors;
import melnorme.utilbox.concurrency.FutureX;
import melnorme.utilbox.concurrency.ICommonExecutor.CommonFuture;
import melnorme.utilbox.concurrency.ITaskAgent;
import melnorme.utilbox.concurrency.OperationCancellation;
import melnorme.utilbox.core.CommonException;
import melnorme.utilbox.core.fntypes.CallableX;
import melnorme.utilbox.core.fntypes.OperationCallable;

/**
 * Manages launching D tools.
 * Has an executor agent to run external DUB commands.
 */
public class DeeToolManager extends ToolManager {
	
	protected final ITaskAgent dubProcessAgent = CoreExecutors.newExecutorTaskAgent("DDT.DubProcessAgent");
	
	public DeeToolManager(CoreSettings settings) {
		super(settings);
	}
	
	@Override
	public void shutdownNow() {
		dubProcessAgent.shutdownNowAndCancelAll();
	}
	
	/* -----------------  ----------------- */
	
	public <R, X extends Exception> FutureX<R, X> submitTask2(CallableX<R, X> task) {
		return dubProcessAgent.submitX(task);
	}
	
	public <R> R submitTaskAndAwaitResult(OperationCallable<R> task) 
			throws CommonException, OperationCancellation {
		CommonFuture<R> future = dubProcessAgent.submitOp(task);
		return future.getResult().get();
	}
	
}