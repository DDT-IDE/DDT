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

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * An {@link ExecutorAgent} is an executor with a single execution thread, 
 * that executes task submited to it in a linear fashion. This is similar to an event loop.  
 */
public class ExecutorAgent implements ExecutorService {
	
	protected final ThreadPoolExecutor executor;
	protected final String name;
	
	public ExecutorAgent(String name) {
		this.name = name;
		this.executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), 
				new NameAgentThreadFactory(name)) {
			
			@Override
			protected void afterExecute(Runnable r, Throwable t) {
				afterExecuteDo(r, t);
			}
		};
	}
	
	public String getName() {
		return name;
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
		Future<?> waiter = executor.submit(new Runnable() {
			
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
	
	protected void afterExecuteDo(Runnable runnable, Throwable throwable) {
		
		if (throwable == null && runnable instanceof Future) {
			try {
				Future<?> future = (Future<?>) runnable;
				future.get();
			} catch (CancellationException ce) {
				throwable = ce;
			} catch (ExecutionException ee) {
				throwable = ee.getCause();
			} catch (InterruptedException ie) {
				throwable = ie;
			}
		}
		
		handleUnexpectException(throwable);
		
	}
	
	@SuppressWarnings("unused")
	protected void handleUnexpectException(Throwable throwable) {
	}
	
	/* ---- delegate all methods --- */
	
	@Override
	public void shutdown() {
		executor.shutdown();
	}
	
	@Override
	public void execute(Runnable command) {
		executor.execute(command);
	}
	
	@Override
	public List<Runnable> shutdownNow() {
		return executor.shutdownNow();
	}
	
	@Override
	public boolean isShutdown() {
		return executor.isShutdown();
	}
	
	@Override
	public boolean isTerminated() {
		return executor.isTerminated();
	}
	
	@Override
	public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
		return executor.awaitTermination(timeout, unit);
	}
	
	@Override
	public <T> Future<T> submit(Callable<T> task) {
		return executor.submit(task);
	}
	
	@Override
	public <T> Future<T> submit(Runnable task, T result) {
		return executor.submit(task, result);
	}
	
	@Override
	public Future<?> submit(Runnable task) {
		return executor.submit(task);
	}
	
	@Override
	public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
		return executor.invokeAll(tasks);
	}
	
	@Override
	public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
			throws InterruptedException {
		return executor.invokeAll(tasks, timeout, unit);
	}
	
	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
		return executor.invokeAny(tasks);
	}
	
	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
			throws InterruptedException, ExecutionException, TimeoutException {
		return executor.invokeAny(tasks, timeout, unit);
	}
	
}