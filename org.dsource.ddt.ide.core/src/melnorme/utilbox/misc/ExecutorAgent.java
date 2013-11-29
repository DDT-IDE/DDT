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
package melnorme.utilbox.misc;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * An {@link ExecutorAgent} is an executor with a single execution thread, 
 * that executes task submited to it in a linear fashion. This is similar to an event loop.  
 */
public class ExecutorAgent extends ThreadPoolExecutor {
	
	public ExecutorAgent(String name) {
		super(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), 
				new NameAgentThreadFactory(name));
		Executors.newSingleThreadExecutor();
		
	}
	
	/** Modified from DefaultThreadFactory to allow setting an unique thread name */
    protected static class NameAgentThreadFactory implements ThreadFactory {
        private final ThreadGroup group;
        private final String name;

        NameAgentThreadFactory(String name) {
            this.name = name;
            SecurityManager s = System.getSecurityManager();
            this.group  = (s != null) ? s.getThreadGroup() :
                                  Thread.currentThread().getThreadGroup();
        }
        
        @Override
		public Thread newThread(Runnable r) {
        	
            Thread t = new Thread(group, r, name, 0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }

	public void waitForPendingTasks() {
		Future<?> waiter = submit(new Runnable() {
			
			@Override
			public void run() {
			}
		});
		try {
			waiter.get();
		} catch (InterruptedException | ExecutionException e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
	}
	
	@Override
	protected void afterExecute(Runnable r, Throwable t) {
		super.afterExecute(r, t);
	}
	
}