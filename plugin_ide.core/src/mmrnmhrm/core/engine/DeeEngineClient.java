/*******************************************************************************
 * Copyright (c) 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.core.engine;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.io.IOException;

import dtool.engine.DToolServer;
import dtool.engine.ModuleParseCache;
import dtool.engine.ModuleParseCache.CachedModuleEntry;
import dtool.engine.SemanticManager;
import dtool.engine.operations.DeeSymbolCompletionResult;
import dtool.engine.operations.FindDefinitionResult;
import dtool.parser.DeeParserResult.ParsedModule;
import dtool.parser.structure.DeeStructureCreator;
import melnorme.lang.ide.core.LangCore;
import melnorme.lang.ide.core.engine.EngineOperation;
import melnorme.lang.ide.core.engine.SourceModelManager;
import melnorme.lang.tooling.ops.IOperationMonitor;
import melnorme.lang.tooling.structure.SourceFileStructure;
import melnorme.utilbox.concurrency.OperationCancellation;
import melnorme.utilbox.core.CommonException;
import melnorme.utilbox.misc.Location;

/**
 * Handle communication with DToolServer.
 */
public class DeeEngineClient extends SourceModelManager {
	
	public static DeeEngineClient getDefault() {
		return LangCore.getSourceModelManager();
	}
	
	protected final DToolServer dtoolServer;
	
	public DeeEngineClient() {
		super();
		
		dtoolServer = new DToolServer() {
			@Override
			public void logError(String message, Throwable throwable) {
				super.logError(message, throwable);
				// Note: the error logging is important not just logging in normal usage, 
				// but also for tests detecting errors. It's not the best way, but works for now.
				LangCore.logError(message, throwable);
			}
		};
	}
	
	public SemanticManager getServerSemanticManager() {
		return dtoolServer.getSemanticManager();
	}
	
	protected ModuleParseCache getParseCache() {
		return getServerSemanticManager().getParseCache();
	}
	
	/* -----------------  ----------------- */
	
	public abstract class WorkingCopyStructureUpdateTask extends StructureUpdateTask {
		
		protected final Location fileLocation; // can be null
		
		public WorkingCopyStructureUpdateTask(StructureInfo structureInfo) {
			super(structureInfo);
			this.fileLocation = structureInfo.getLocation();
		}
		
		@Override
		protected SourceFileStructure createNewData() {
			ParsedModule parsedModule = (fileLocation == null) ? 
					parseModuleWithNoLocation() :
					setWorkingSourceAndParseModule(fileLocation);
			
			if(parsedModule == null || isCancelled()) {
				return null;
			}
			return new DeeStructureCreator().createStructure(parsedModule, fileLocation);
		}
		
		/**
		 * @param fileLocation non-null
		 */
		protected ParsedModule setWorkingSourceAndParseModule(Location fileLocation) {
			final CachedModuleEntry entry = getParseCache().getEntry(fileLocation.toPath());
			entry.runUnderEntryLock(new Runnable() {
				@Override
				public void run() {
					if(!isCancelled()) {
						modifyWorkingSource(entry);
					}
				}
			});
			
			if(isCancelled()) {
				return null;
			}
			try {
				return parseModuleFromWorkingCopy(entry);
			} catch(OperationCancellation e) {
				return null;
			}
		}
		
		protected abstract ParsedModule parseModuleWithNoLocation();
		
		protected abstract void modifyWorkingSource(CachedModuleEntry lockedEntry);
		
		protected abstract ParsedModule parseModuleFromWorkingCopy(CachedModuleEntry entry) 
				throws OperationCancellation;
		
	}
	
	@Override
	protected StructureUpdateTask createUpdateTask(StructureInfo structureInfo, String source) {
		
		assertNotNull(source);
		
		return new WorkingCopyStructureUpdateTask(structureInfo) {
			
			@Override
			protected ParsedModule parseModuleWithNoLocation() {
				try {
					return getParseCache().parseModuleWithNoLocation(source, cm);
				} catch(OperationCancellation e) {
					return null;
				}
			}
			
			@Override
			protected void modifyWorkingSource(CachedModuleEntry lockedEntry) {
				lockedEntry.setWorkingSource(source);
			}
			
			@Override
			protected ParsedModule parseModuleFromWorkingCopy(CachedModuleEntry entry) throws OperationCancellation {
				try {
					return entry.getParsedModule(cm);
				} catch(IOException e) {
					return null;
				}
			}
		};
	}
	
	@Override
	protected DisconnectUpdatesTask createDisconnectTask(StructureInfo structureInfo) {
		return new DisconnectUpdatesTask(structureInfo) {
			@Override
			protected void handleDisconnectForLocation(Location location) {
				getParseCache().discardWorkingCopy(location.toPath());
			}
		};
	}
	
	/* ----------------- operations ----------------- */
	
	// Only tests may modify this variable:
	public static volatile Location compilerPathOverride = null;
	
	public class CodeCompletionOperation extends EngineOperation<DeeSymbolCompletionResult> {
		
		protected final String effectiveDubPath;
		
		public CodeCompletionOperation(Location location, int timeoutMillis, int offset, String effectiveDubPath) {
			super(DeeEngineClient.this, location, offset, timeoutMillis, "Code Completion");
			this.effectiveDubPath = effectiveDubPath;
		}
		
		@Override
		protected DeeSymbolCompletionResult doRunOperationWithWorkingCopy(IOperationMonitor om) 
				throws CommonException, OperationCancellation {
			return dtoolServer.doCodeCompletion(location.toPath(), offset, DeeEngineClient.compilerPathOverride, 
				effectiveDubPath);
		}
		
	}
	
	public class FindDefinitionOperation extends EngineOperation<FindDefinitionResult> {
		
		protected final String effectiveDubPath;
		
		public FindDefinitionOperation(Location location, int offset, int timeoutMillis, String effectiveDubPath) {
			super(DeeEngineClient.this, location, offset, timeoutMillis, "Find Definition");
			this.effectiveDubPath = effectiveDubPath;
		}
		
		@Override
		protected FindDefinitionResult doRunOperationWithWorkingCopy(IOperationMonitor om) 
				throws CommonException, OperationCancellation {
			return dtoolServer.doFindDefinition(location.toPath(), offset, effectiveDubPath);
		}
	}
	
	public class FindDDocViewOperation extends EngineOperation<String> {
		
		protected final String effectiveDubPath;
		
		public FindDDocViewOperation(Location location, int offset, int timeoutMillis, String effectiveDubPath) {
			super(DeeEngineClient.this, location, offset, timeoutMillis, "Resolve DDoc");
			this.effectiveDubPath = effectiveDubPath;
		}
		
		@Override
		protected String doRunOperationWithWorkingCopy(IOperationMonitor om) 
				throws CommonException, OperationCancellation {
			return dtoolServer.getDDocHTMLView(location.toPath(), offset, effectiveDubPath);
		}
	}
	
}