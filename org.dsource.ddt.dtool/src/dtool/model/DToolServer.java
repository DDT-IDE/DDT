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
	
	protected static DToolServer instance;
	
	
	/* ----------------- ----------------- */
	
	protected final ITaskAgent dubProcessAgent = new ExecutorTaskAgent("DSE.DubProcessAgent") {
		@Override
		protected void handleUnexpectedException(Throwable throwable) {
			logError("Unhandled exception in dub agent thread.", throwable);
		};
	};
	
	protected final BundleManifestRegistry bundleManifestRegistry = new BundleManifestRegistry(this);
	protected final SemanticManager semanticManager = new SemanticManager(this);
	
	
	public DToolServer() {
		logMessage("DTool started");
	}
	
	protected void shutdown() {
		dubProcessAgent.shutdownNow();
	}
	
	public SemanticManager getSemanticManager() {
		return semanticManager;
	}
	
	public BundleManifestRegistry getBundleManifestRegistry() {
		return bundleManifestRegistry;
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