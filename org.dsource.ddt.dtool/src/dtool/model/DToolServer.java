/*******************************************************************************
 * Copyright (c) 2014, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.model;

import melnorme.utilbox.concurrency.ExecutorTaskAgent;
import melnorme.utilbox.concurrency.ITaskAgent;

public class DToolServer {
	
	protected static final DToolServer instance = new DToolServer();
	
	protected static DToolServer getInstance() {
		return instance;
	}
	
	protected static ITaskAgent getProcessAgent() {
		return getInstance().dubProcessAgent;
	}
	
	/* ----------------- ----------------- */
	
	protected final ITaskAgent dubProcessAgent = new ExecutorTaskAgent(getClass().getSimpleName()) {
		@Override
		protected void handleUnexpectedException(Throwable throwable) {
			logError("Unhandled exception in dub agent thread.", throwable);
		};
	};
	
	protected final SemanticManager semanticManager = new SemanticManager(dubProcessAgent);
	
	public DToolServer() {
		logMessage("DTool started");
	}
	
	public void shutdown() {
		dubProcessAgent.shutdownNow();
	}
	
	protected void logMessage(String message) {
		System.out.println("> " + message);
	}
	
	protected void logError(String message, Throwable throwable) {
		System.err.println(">> " + message);
		if(throwable != null) {
			System.err.println(throwable);
		}
	}
	
}