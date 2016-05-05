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

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.eclipse.core.runtime.CoreException;

import melnorme.lang.ide.core.LangCore;
import melnorme.lang.ide.core.operations.ToolManager;
import melnorme.lang.ide.core.utils.CoreExecutors;
import melnorme.utilbox.concurrency.ITaskAgent;
import melnorme.utilbox.concurrency.OperationCancellation;
import melnorme.utilbox.core.ExceptionAdapter;

/**
 * Manages launching D tools.
 * Has an executor agent to run external DUB commands.
 */
public class DeeToolManager extends ToolManager {
	
	protected final ITaskAgent dubProcessAgent = CoreExecutors.newExecutorTaskAgent("DDT.DubProcessAgent");
	
	public DeeToolManager() {
	}
	
	@Override
	public void shutdownNow() {
		dubProcessAgent.shutdownNowAndCancelAll();
	}
	
	/* -----------------  ----------------- */
	
	public <T> Future<T> submitTask(Callable<T> task) {
		return dubProcessAgent.submit(task);
	}
	
	public <T> T submitTaskAndAwaitResult(Callable<T> task) throws CoreException, OperationCancellation {
		Future<T> future = dubProcessAgent.submit(task);
		try {
			return future.get();
		} catch (InterruptedException e) {
			future.cancel(true);
			LangCore.logError("Unexpected interruption", e);
			throw new OperationCancellation();
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();
			if(cause instanceof OperationCancellation) {
				throw (OperationCancellation) cause;
			}
			if(cause instanceof CoreException) {
				throw (CoreException) cause;
			}
			throw ExceptionAdapter.unchecked(cause); // Should not happen
		}
	}
	
}