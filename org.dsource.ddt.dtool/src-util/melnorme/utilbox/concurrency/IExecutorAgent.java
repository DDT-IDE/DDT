/*******************************************************************************
 * Copyright (c) 2013, 2013 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.utilbox.concurrency;

import java.util.concurrent.ExecutorService;

/**
 * An {@link ExecutorAgent} executes tasks submitted to it in sequence,
 * by means of a single execution thread running in the background. 
 * It works in a way similar to an event loop.  
 */
public interface IExecutorAgent extends ExecutorService {
	
	/** 
	 * Wait for all tasks that have been submitted so far to complete.
	 */
	void waitForPendingTasks();
	
    /**
     * Returns the approximate total number of tasks that have ever been
     * scheduled for execution. Because the states of tasks and
     * threads may change dynamically during computation, the returned
     * value is only an approximation.
     *
     * @return the number of tasks
     */
	long getSubmittedTaskCount();
	
	/**
	 * Indefinitely wait for the executor to terminate.
	 * @throws InterruptedException if interrupted.
	 */
	void awaitTermination() throws InterruptedException;
	
}