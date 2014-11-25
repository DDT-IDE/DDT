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
package dtool.engine;

import java.nio.file.Path;
import java.util.concurrent.ExecutionException;

import melnorme.lang.tooling.engine.completion.CompletionSearchResult;
import melnorme.utilbox.concurrency.ExecutorTaskAgent;
import dtool.engine.operations.CodeCompletionOperation;
import dtool.engine.operations.FindDefinitionOperation;
import dtool.engine.operations.FindDefinitionResult;
import dtool.engine.operations.ResolveDocViewOperation;

public class DToolServer {
	
	protected final SemanticManager semanticManager = createSemanticManager();
	
	public DToolServer() {
		logMessage(" ------ DTool engine started ------ ");
	}
	
	protected SemanticManager createSemanticManager() {
		return new SemanticManager(this);
	}
	
	public SemanticManager getSemanticManager() {
		return semanticManager;
	}
	
	protected void shutdown() {
		semanticManager.shutdown();
	}
	
	public void logMessage(String message) {
		System.out.println("> " + message);
	}
	
	public final void logError(String message) {
		logError(message, null);
	}
	public void logError(String message, Throwable throwable) {
		System.out.println("!! " + message);
		if(throwable != null) {
			System.out.println(throwable);
		}
	}
	
	public void handleInternalError(Throwable throwable) {
		logError("!!!! INTERNAL ERRROR: ", throwable);
		throwable.printStackTrace(System.err);
	}
	
	
	protected ResolvedModule getResolvedModule(Path filePath) throws ExecutionException {
		return getSemanticManager().getUpdatedResolvedModule(filePath);
	}
	
	/* -----------------  ----------------- */
	
	public class DToolTaskAgent extends ExecutorTaskAgent {
		public DToolTaskAgent(String name) {
			super(name);
		}
		
		@Override
		protected void handleUnexpectedException(Throwable throwable) {
			handleInternalError(throwable);
		}
	}
	
	/* ----------------- Operations ----------------- */
	
	public FindDefinitionResult doFindDefinition(Path filePath, final int offset) {
		return new FindDefinitionOperation(getSemanticManager()).findDefinition(filePath, offset);
	}
	
	public String getDDocHTMLView(Path filePath, int offset) {
		return new ResolveDocViewOperation(getSemanticManager(), filePath, offset).perform();
	}
	
	public CompletionSearchResult doCodeCompletion(Path filePath, int offset, Path compilerPath) 
			throws ExecutionException {
		return new CodeCompletionOperation(getSemanticManager()).doCodeCompletion(filePath, offset, compilerPath);
	}
	
}