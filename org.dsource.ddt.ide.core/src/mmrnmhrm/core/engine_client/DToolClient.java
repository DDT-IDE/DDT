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
import java.util.HashSet;
import java.util.concurrent.ExecutionException;

import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.model_elements.DeeSourceElementProvider;
import mmrnmhrm.core.model_elements.ModelDeltaVisitor;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.compiler.ISourceElementRequestor;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.compiler.problem.IProblemReporter;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.ElementChangedEvent;
import org.eclipse.dltk.core.IModelElementDelta;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;

import dtool.ast.definitions.Module;
import dtool.dub.BundlePath;
import dtool.engine.BundleResolution.CommonResolvedModule;
import dtool.engine.DToolServer;
import dtool.engine.ModuleParseCache;
import dtool.engine.ModuleParseCache.ParseSourceException;
import dtool.engine.SemanticManager;
import dtool.parser.DeeParserResult.ParsedModule;
import dtool.resolver.PrefixDefUnitSearch;
import dtool.resolver.api.FindDefinitionResult;

/**
 * Handle communication with DToolServer.
 */
public class DToolClient {
	
	public static DToolClient getDefault() {
		return DeeCore.getDToolClient();
	}
	
	public static DToolClient initializeNew() {
		return new DToolClient();
	}
	
	protected final DToolServer dtoolServer;
	protected final ModuleParseCache moduleParseCache;
	
	protected final WorkingCopyListener wclistener = new WorkingCopyListener();
	
	public DToolClient() {
		dtoolServer = new DToolServer() {
			@Override
			protected void logError(String message, Throwable throwable) {
				super.logError(message, throwable);
				// Note: the error logging is important not just logging in normal usage, 
				// but also for tests detecting errors. It's not the best way, but works for now.
				DeeCore.logError(message, throwable);
			}
		};
		moduleParseCache = new ModuleParseCache(dtoolServer);
		DLTKCore.addElementChangedListener(wclistener, ElementChangedEvent.POST_CHANGE);
	}
	
	public void shutdown() {
		DLTKCore.removeElementChangedListener(wclistener);
	}
	
	protected ModuleParseCache getClientModuleCache() {
		return moduleParseCache;
	}
	
	protected SemanticManager getServerSemanticManager() {
		return dtoolServer.getSemanticManager();
	}
	
	public ParsedModule getParsedModuleOrNull(Path filePath) {
		try {
			return getClientModuleCache().getParsedModule(filePath);
		} catch (ParseSourceException e) {
			// Most likely a file IO error ocurred. 
			DeeCore.logWarning("Error in getParsedModule", e);
			return null;
		}
	}
	public ParsedModule getParsedModuleOrNull_withSource(Path filePath, IModuleSource input) {
		if(filePath == null) { 
			return null;
		}
		if(!filePath.isAbsolute()) {
			// If it's a special path, there will not be an underlying file, so we must retrieve source directly.
			return getClientModuleCache().getParsedModule(filePath, input.getSourceContents());
		}
		return getParsedModuleOrNull(filePath);
	}
	
	public ParsedModule getExistingParsedModuleOrNull(Path filePath) {
		return getClientModuleCache().getExistingParsedModule(filePath);
	}
	public Module getExistingParsedModuleNodeOrNull(Path filePath) {
		ParsedModule parsedModule = getExistingParsedModuleOrNull(filePath);
		return parsedModule == null ? null : parsedModule.module;
	}
	
	public static Path getPathHandleForModuleSource(IModuleSource input) {
		return DToolClient_Bad.getFilePathOrNull(input);
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
				getServerSemanticManager().updateWorkingCopyAndParse(filePath, source);
				return getClientModuleCache().getParsedModule(filePath, source);
			} else {
				// This method can be called during the scope of the discard/commit working copy method,
				// and as such the WorkingCopyListener has not yet had a chance to discard the cache working.
				// Because of that, we should check here as well if it's a WC, and discard it if so.
				boolean isWorkingCopy = sourceModule.isWorkingCopy();
				if(!isWorkingCopy) {
					getClientModuleCache().discardWorkingCopy(filePath);
				}
				return getClientModuleCache().getParsedModule(filePath);
			}
		} catch (ParseSourceException | ModelException e) {
			DeeCore.logWarning("Error in parseModule", e);
			return null;
		}
	}
	
	protected class WorkingCopyListener extends ModelDeltaVisitor {
		
		@Override
		public void visitSourceModule(IModelElementDelta moduleDelta, ISourceModule sourceModule) {
			if((moduleDelta.getFlags() & IModelElementDelta.F_PRIMARY_WORKING_COPY) != 0) {
				if(sourceModule.isWorkingCopy() == false) {
					Path filePath = DToolClient_Bad.getFilePathOrNull(sourceModule);
					if(filePath != null) {
						// We update the server working copy too.
						getServerSemanticManager().discardWorkingCopy(filePath);
						getClientModuleCache().discardWorkingCopy(filePath);
					}
				}
			}
		}
		
	}
	
	public void updateWorkingCopyIfInconsistent(Path filePath, String source, ISourceModule sourceModule) {
		try {
			if(sourceModule.isConsistent()) {
				return;
			}
		} catch (ModelException e) {
		}
		getServerSemanticManager().updateWorkingCopyAndParse(filePath, source);
	}
	
	/* -----------------  ----------------- */
	
	public PrefixDefUnitSearch doCodeCompletion(IModuleSource moduleSource, int offset) throws CoreException {
		
		if(moduleSource instanceof ISourceModule) {
			ISourceModule sourceModule = (ISourceModule) moduleSource;
			return doCodeCompletion(sourceModule, offset);
		}
		
		Path filePath = DToolClient_Bad.getFilePath(moduleSource);
		// Update source to engine server.
		if(filePath != null) {
			String sourceContents = moduleSource.getSourceContents();
			getServerSemanticManager().updateWorkingCopyAndParse(filePath, sourceContents);
		}
		return doCodeCompletion_Do(filePath, offset);
	}
	
	public PrefixDefUnitSearch doCodeCompletion(ISourceModule sourceModule, int offset) throws CoreException {
		Path filePath = DToolClient_Bad.getFilePath(sourceModule);
		
		// Submit latest source to engine server.
		updateWorkingCopyIfInconsistent(filePath, sourceModule.getSource(), sourceModule);
		
		return doCodeCompletion_Do(filePath, offset);
	}
	
	protected PrefixDefUnitSearch doCodeCompletion_Do(Path filePath, int offset) throws CoreException {
		if(filePath == null) { 
			throw DeeCore.createCoreException("Invalid path for content assist source.", null);
		}
		
		return doCodeCompletion_Client(filePath, offset);
	}
	
	protected PrefixDefUnitSearch doCodeCompletion_Client(Path filePath, int offset) throws CoreException {
		CommonResolvedModule resolvedModule;
		try {
			resolvedModule = getResolvedModule(filePath);
		} catch (ExecutionException e) {
			throw DeeCore.createCoreException("Error performing code complete operation.", e.getCause());
		}
		return PrefixDefUnitSearch.doCompletionSearch(resolvedModule.getParsedModule(), offset, 
			resolvedModule.getModuleResolver());
	}
	
	public HashSet<String> listModulesFor(IProject project, String fullNamePrefix) throws CoreException {
		BundlePath bundlePath = BundlePath.create(project.getLocation().toFile().toPath());
		try {
			return dtoolServer.getSemanticManager().getUpdatedResolution(bundlePath).findModules2(fullNamePrefix);
		} catch (ExecutionException e) {
			throw new CoreException(DeeCore.createErrorStatus("DToolClient error: ", e.getCause()));
		}
	}
	
	public CommonResolvedModule getResolvedModule(Path filePath) throws ExecutionException {
		return dtoolServer.getSemanticManager().getUpdatedResolvedModule(filePath);
	}
	
	public FindDefinitionResult doFindDefinition(Path filePath, int offset) {
		return dtoolServer.doFindDefinition(filePath, offset);
	}
	
	public String getDDocHTMLView(Path filePath, int offset) {
		return dtoolServer.getDDocHTMLView(filePath, offset);
	}
	
}