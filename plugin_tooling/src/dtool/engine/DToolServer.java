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
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutionException;

import melnorme.lang.tooling.engine.completion.CompletionSearchResult;
import melnorme.utilbox.concurrency.ExecutorTaskAgent;
import melnorme.utilbox.misc.Location;
import dtool.engine.compiler_installs.CompilerInstall;
import dtool.engine.compiler_installs.CompilerInstallDetector;
import dtool.engine.compiler_installs.SearchCompilersOnPathOperation;
import dtool.engine.operations.CodeCompletionOperation;
import dtool.engine.operations.FindDefinitionOperation;
import dtool.engine.operations.FindDefinitionResult;
import dtool.engine.operations.ResolveDocViewOperation;

public class DToolServer {
	
	public static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("HH:mm:ss.SSS");
	
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
	
	public CompletionSearchResult doCodeCompletion(Path filePath, int offset, Location compilerPath) 
			throws ExecutionException {
		return new CodeCompletionOperation(getSemanticManager(), compilerPath).doCodeCompletion(filePath, offset);
	}
	
	/* ----------------- helpers ----------------- */
	
	@Deprecated
	public ResolvedModule getUpdatedResolvedModule(Path filePath) throws ExecutionException {
		CompilerInstall compilerInstall = findBestCompilerInstall(null);
		return getUpdatedResolvedModule(filePath, compilerInstall);
	}
	
	public ResolvedModule getUpdatedResolvedModule(Path filePath, CompilerInstall compilerInstall) 
			throws ExecutionException {
		return semanticManager.getUpdatedResolvedModule(filePath, compilerInstall);
	}
	
	public CompilerInstall findBestCompilerInstall(Location compilerPath) {
		CompilerInstall compilerInstall = getCompilerInstallForPath(compilerPath);
		if(compilerInstall == null) {
			SM_SearchCompilersOnPath compilersSearch = new SM_SearchCompilersOnPath();
			compilerInstall = compilersSearch.searchForCompilersInDefaultPathEnvVars().getPreferredInstall();
		}
		return compilerInstall;
	}
	
	public static CompilerInstall getCompilerInstallForPath(Location compilerPath) {
		CompilerInstall compilerInstall = null;
		if(compilerPath != null) {
			return new CompilerInstallDetector().detectInstallFromCompilerCommandPath(compilerPath);
		}
		return compilerInstall;
	}
	
	protected class SM_SearchCompilersOnPath extends SearchCompilersOnPathOperation {
		@Override
		protected void handleWarning(String message) {
			DToolServer.this.logMessage(message);
		}
	}
	
}