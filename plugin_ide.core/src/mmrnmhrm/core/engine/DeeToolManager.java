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
import melnorme.utilbox.concurrency.Future2;
import melnorme.utilbox.concurrency.ITaskAgent;
import melnorme.utilbox.concurrency.OperationCancellation;
import melnorme.utilbox.core.CommonException;
import melnorme.utilbox.core.fntypes.OperationCallable;
import melnorme.utilbox.core.fntypes.OperationResult;

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
	
	/* FIXME: review, make CancellableTask */
	public <R> R submitTaskAndAwaitResult(OperationCallable<R> task) 
			throws CommonException, OperationCancellation {
		// TODO: to allow task to be cancelled by the future (if shutting down for example)
		// it needs to be a ICancellableTask
		Future2<OperationResult<R>> future = dubProcessAgent.submitBasicCallable(task::callToResult);
		return future.awaitResult2().get();
	}
	
}