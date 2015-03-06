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
package mmrnmhrm.core.engine_client;

import static melnorme.utilbox.core.CoreUtil.tryCast;

import java.nio.file.Path;

import melnorme.lang.tooling.context.ModuleSourceException;
import melnorme.lang.tooling.engine.completion.CompletionSearchResult;
import melnorme.utilbox.core.CommonException;
import melnorme.utilbox.misc.Location;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.DeeCorePreferences;
import mmrnmhrm.core.model_elements.DeeSourceElementProvider;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.compiler.ISourceElementRequestor;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.compiler.problem.IProblemReporter;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;

import dtool.engine.DToolServer;
import dtool.engine.ModuleParseCache;
import dtool.engine.SemanticManager;
import dtool.engine.operations.FindDefinitionResult;
import dtool.parser.DeeParserResult.ParsedModule;

/**
 * Handle communication with DToolServer.
 */
public class DToolClient extends AbstractSemanticDaemonClient {
	
	public static DToolClient getDefault() {
		return DeeCore.getDToolClient();
	}
	
	public static ClientModuleParseCache getDefaultModuleCache() {
		return DeeCore.getDToolClient().getClientModuleCache();
	}
	
	public static DToolClient initializeNew() {
		return new DToolClient();
	}
	
	protected final DToolServer dtoolServer;
	protected final ClientModuleParseCache moduleParseCache;
	
	public DToolClient() {
		super();
		
		dtoolServer = new DToolServer() {
			@Override
			public void logError(String message, Throwable throwable) {
				super.logError(message, throwable);
				// Note: the error logging is important not just logging in normal usage, 
				// but also for tests detecting errors. It's not the best way, but works for now.
				DeeCore.logError(message, throwable);
			}
		};
		moduleParseCache = new ClientModuleParseCache(dtoolServer);
	}
	
	@Override
	public void shutdown() {
		super.shutdown();
	}
	
	public ClientModuleParseCache getClientModuleCache() {
		return moduleParseCache;
	}
	
	protected SemanticManager getServerSemanticManager() {
		return dtoolServer.getSemanticManager();
	}
	
	public static class ClientModuleParseCache extends ModuleParseCache {
		
		protected ClientModuleParseCache(DToolServer dtoolServer) {
			super(dtoolServer);
		}
		
		public ParsedModule getParsedModuleOrNull(Path filePath) {
			try {
				return getParsedModule(filePath);
			} catch (ModuleSourceException e) {
				// Most likely a file IO error ocurred. 
				DeeCore.logWarning("Error in getParsedModule", e);
				return null;
			}
		}
		public ParsedModule getParsedModuleOrNull(Path filePath, IModuleSource input) {
			if(filePath == null) { 
				return null;
			}
			if(!filePath.isAbsolute()) {
				// If it's a special path, there will not be an underlying file, so we must retrieve source directly.
				return setWorkingCopyAndGetParsedModule(filePath, input.getSourceContents());
			}
			return getParsedModuleOrNull(filePath);
		}
		
	}
	
	/* ----------------- Module build structure operation and working copy handling ----------------- */
	
	public ParsedModule doParseForBuildStructureOrIndex(IModuleSource input, IProblemReporter reporter) {
		ParsedModule parsedModule = getParsedModuleOrNull_fromBuildStructure(input);
		DeeSourceParserFactory.reportErrors(reporter, parsedModule);
		return parsedModule;
	}
	
	public void provideModelElements(IModuleSource moduleSource, IProblemReporter pr, 
			ISourceElementRequestor requestor) {
		ParsedModule parsedModule = doParseForBuildStructureOrIndex(moduleSource, pr);
		if (parsedModule != null) {
			new DeeSourceElementProvider(requestor).provide(parsedModule);
		}
	}
	
	public ParsedModule getParsedModuleOrNull_fromBuildStructure(IModuleSource input) {
		ISourceModule sourceModule = tryCast(input, ISourceModule.class);
		if(sourceModule == null) {
			sourceModule = tryCast(input.getModelElement() , ISourceModule.class);
		} 
		if(sourceModule != null) {
			return getParsedModuleOrNull_fromWorkingCopy(sourceModule);
		}
		DeeCore.logError("getParsedModuleOrNull_fromBuildStructure: input not a source Module");
		return null;
	}
	
	public ParsedModule getParsedModuleOrNull_fromWorkingCopy(ISourceModule sourceModule) {
		Path filePath = DToolClient_Bad.getFilePathOrNull(sourceModule);
		if(filePath == null) 
			return null;
		
		try {
			if(!sourceModule.isConsistent()) {
				// This usually means it's a working copy, but its not guaranteed.
				String source = sourceModule.getSource();
				// We update the server working copy too.
				getServerSemanticManager().setWorkingCopyAndParse(filePath, source);
				return getClientModuleCache().setWorkingCopyAndGetParsedModule(filePath, source);
			} else {
				// This method can be called during the scope of the discard/commit working copy method,
				// and as such the WorkingCopyListener has not yet had a chance to discard the cache working.
				// Because of that, we should check here as well if it's a WC, and discard it if so.
				boolean isWorkingCopy = sourceModule.isWorkingCopy();
				if(!isWorkingCopy) {
					discardWorkingCopy(filePath);
				}
				return getClientModuleCache().getParsedModule(filePath);
			}
		} catch (ModuleSourceException | ModelException e) {
			DeeCore.logWarning("Error in parseModule", e);
			return null;
		}
	}
	
	@Override
	protected void discardWorkingCopy(Path filePath) {
		discardServerWorkingCopy(filePath);
		getClientModuleCache().discardWorkingCopy(filePath);
	}
	
	/** Warning: some code must later be responsible for disposing of the server's working copy */
	@Override
	public void updateServerWorkingCopy(Path filePath, String source) {
		getServerSemanticManager().setWorkingCopyAndParse(filePath, source);
	}
	
	@Override
	public void discardServerWorkingCopy(Path filePath) {
		getServerSemanticManager().discardWorkingCopy(filePath);
	}
	
	public CompletionSearchResult performCompletionOperation(final Path filePath, final int offset, 
			String source, final int timeoutMillis) throws CoreException {
		
		return new CodeCompletionOperation(filePath, source, timeoutMillis, offset).runSemanticServerOperation();
	}
	
	public class CodeCompletionOperation extends SemanticEngineOperation<CompletionSearchResult> {
		
		public CodeCompletionOperation(Path filePath, String source, int timeoutMillis, int offset) {
			super(filePath, source, offset, timeoutMillis, "Code Completion");
		}
		
		@Override
		protected CompletionSearchResult doRunOperationWithWorkingCopy() throws CoreException {
			return doCodeCompletion(filePath, offset, DeeCompletionOperation.compilerPathOverride);
		}
		
	}
	
	public class FindDefinitionOperation extends SemanticEngineOperation<FindDefinitionResult> {
		
		public FindDefinitionOperation(Path filePath, String source, int offset, int timeoutMillis) {
			super(filePath, source, offset, timeoutMillis, "Find Definition");
		}
		
		@Override
		protected FindDefinitionResult doRunOperationWithWorkingCopy() throws CoreException {
			return doFindDefinition(filePath, offset);
		}
	}
	
	public class FindDDocViewOperation extends SemanticEngineOperation<String> {
		
		public FindDDocViewOperation(Path filePath, String source, int offset, int timeoutMillis) {
			super(filePath, source, offset, timeoutMillis, "Resolve DDoc");
		}
		
		@Override
		protected String doRunOperationWithWorkingCopy() throws CoreException {
			return getDDocHTMLView(filePath, offset);
		}
	}
	
	/* ----------------- Engine client requests ----------------- */
	
	
	protected CompletionSearchResult doCodeCompletion(Path filePath, int offset, Location compilerPath) 
			throws CoreException {
		try {
			return dtoolServer.doCodeCompletion(filePath, offset, compilerPath, 
				DeeCorePreferences.getEffectiveDubPath());
		} catch (CommonException e) {
			throw DeeCore.createCoreException(e);
		}
	}
	
	protected FindDefinitionResult doFindDefinition(Path filePath, int offset) throws CoreException {
		try {
			return dtoolServer.doFindDefinition(filePath, offset, DeeCorePreferences.getEffectiveDubPath());
		} catch (CommonException e) {
			throw DeeCore.createCoreException(e);
		}
	}
	
	protected String getDDocHTMLView(Path filePath, int offset) throws CoreException {
		try {
			return dtoolServer.getDDocHTMLView(filePath, offset, DeeCorePreferences.getEffectiveDubPath());
		} catch (CommonException e) {
			throw DeeCore.createCoreException(e);
		}
	}
	
}