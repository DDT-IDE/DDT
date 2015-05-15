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
package mmrnmhrm.core.engine;

import melnorme.lang.ide.core.engine.EngineClient;
import melnorme.lang.ide.core.engine.EngineOperation;
import melnorme.lang.ide.core.engine.StructureModelManager.StructureInfo;
import melnorme.lang.ide.core.engine.StructureModelManager.StructureUpdateTask;
import melnorme.lang.tooling.structure.SourceFileStructure;
import melnorme.utilbox.concurrency.OperationCancellation;
import melnorme.utilbox.core.CommonException;
import melnorme.utilbox.misc.Location;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.DeeCorePreferences;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;

import dtool.engine.DToolServer;
import dtool.engine.ModuleParseCache;
import dtool.engine.SemanticManager;
import dtool.engine.operations.DeeSymbolCompletionResult;
import dtool.engine.operations.FindDefinitionResult;
import dtool.parser.DeeParserResult.ParsedModule;
import dtool.parser.structure.DeeStructureCreator;

/**
 * Handle communication with DToolServer.
 */
public class DToolClient extends EngineClient {
	
	public static DToolClient getDefault() {
		return DeeCore.getDToolClient();
	}
	
	protected final DToolServer dtoolServer;
	
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
	}
	
	@Override
	public void shutdown() {
		super.shutdown();
	}
	
	public SemanticManager getServerSemanticManager() {
		return dtoolServer.getSemanticManager();
	}
	
	protected ModuleParseCache getParseCache() {
		return getServerSemanticManager().getParseCache();
	}
	
	/* -----------------  ----------------- */
	
	@Override
	protected StructureUpdateTask createUpdateTask(StructureInfo structureInfo, final Location fileLocation, 
			IDocument document, boolean isDirty) {
		
		/* FIXME: timestamp / concurrency of workingCopy*/
		int requestSequenceNo = requestSequenceCounter++;
		final String source = document.get();
		
		return new StructureUpdateTask(structureInfo) {
			@Override
			protected SourceFileStructure createSourceFileStructure() {
				// FIXME: todo cancel monitor 
				
				final ParsedModule parsedModule;
				if(fileLocation != null) {
					parsedModule = getParseCache().setWorkingCopyAndGetParsedModule(fileLocation.toPath(), source);
				} else {
					parsedModule = getParseCache().parseUnlocatedModule(source);
				}
				return new DeeStructureCreator().createStructure(fileLocation, parsedModule);
			}
		};
	}
	
	@Override
	protected StructureUpdateTask createDisposeTask(StructureInfo structureInfo, Location fileLocation) {
		final Location location = getLocation(structureInfo.getKey());
		
		return new StructureUpdateTask(structureInfo) {
			@Override
			protected SourceFileStructure createSourceFileStructure() {
				/* FIXME: timestamp / concurrency of workingCopy*/
				getParseCache().discardWorkingCopy(location.toPath());
				return null;
			}
		};
	}
	
	/* ----------------- operations ----------------- */
	
	/* FIXME: review to see if still necessary. */
	// Only tests may modify this variable:
	public static volatile Location compilerPathOverride = null;
	
	public class CodeCompletionOperation extends EngineOperation<DeeSymbolCompletionResult> {
		
		public CodeCompletionOperation(Location location, int timeoutMillis, int offset) {
			super(DToolClient.this, location, offset, timeoutMillis, "Code Completion");
		}
		
		@Override
		protected DeeSymbolCompletionResult doRunOperationWithWorkingCopy(IProgressMonitor pm) 
				throws CommonException, CoreException, OperationCancellation {
			return dtoolServer.doCodeCompletion(location.toPath(), offset, DToolClient.compilerPathOverride, 
				DeeCorePreferences.getEffectiveDubPath());
		}
		
	}
	
	public class FindDefinitionOperation extends EngineOperation<FindDefinitionResult> {
		
		public FindDefinitionOperation(Location location, int offset, int timeoutMillis) {
			super(DToolClient.this, location, offset, timeoutMillis, "Find Definition");
		}
		
		@Override
		protected FindDefinitionResult doRunOperationWithWorkingCopy(IProgressMonitor pm) 
				throws CommonException, CoreException, OperationCancellation {
			return dtoolServer.doFindDefinition(location.toPath(), offset, DeeCorePreferences.getEffectiveDubPath());
		}
	}
	
	public class FindDDocViewOperation extends EngineOperation<String> {
		
		public FindDDocViewOperation(Location location, int offset, int timeoutMillis) {
			super(DToolClient.this, location, offset, timeoutMillis, "Resolve DDoc");
		}
		
		@Override
		protected String doRunOperationWithWorkingCopy(IProgressMonitor pm) 
				throws CommonException, CoreException, OperationCancellation {
			return dtoolServer.getDDocHTMLView(location.toPath(), offset, DeeCorePreferences.getEffectiveDubPath());
		}
	}
	
}