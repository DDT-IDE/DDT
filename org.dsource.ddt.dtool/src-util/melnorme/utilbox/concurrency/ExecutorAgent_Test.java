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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.List;
import java.util.concurrent.Future;

import org.junit.Test;

public class ExecutorAgent_Test {
	
	@Test
	public void testShutdownNow() throws Exception {
		ExecutorAgent agent = new ExecutorAgent("blah");
		LatchRunnable firstTask = new LatchRunnable();
		agent.submit(firstTask);
		Future<?> secondTask = agent.submit(firstTask);
		
		firstTask.awaitEntry();
		assertTrue(secondTask.isCancelled() == false);
		
		List<Runnable> cancelledTasks = agent.shutdownNow();
		assertTrue(cancelledTasks.size() == 1);
		
		assertTrue(secondTask.isCancelled() == true);
		assertTrue(agent.isShutdown());
		Thread.sleep(1);
		assertTrue(agent.isTerminating() == true);
		assertTrue(agent.isTerminated() == false);
		firstTask.releaseAll();
		agent.awaitTermination();
		assertTrue(agent.isShutdown());
		assertTrue(agent.isTerminating() == false);
		assertTrue(agent.isTerminated());
	}
	
}