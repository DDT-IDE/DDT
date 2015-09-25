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

import melnorme.lang.ide.core.engine.EngineClient;
import melnorme.lang.ide.core.engine.EngineOperation;
import melnorme.lang.tooling.structure.SourceFileStructure;
import melnorme.utilbox.concurrency.OperationCancellation;
import melnorme.utilbox.core.CommonException;
import melnorme.utilbox.misc.Location;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.DeeCorePreferences;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import dtool.engine.DToolServer;
import dtool.engine.ModuleParseCache;
import dtool.engine.ModuleParseCache.CachedModuleEntry;
import dtool.engine.SemanticManager;
import dtool.engine.operations.DeeSymbolCompletionResult;
import dtool.engine.operations.FindDefinitionResult;
import dtool.parser.DeeParserResult.ParsedModule;
import dtool.parser.structure.DeeStructureCreator;

/**
 * Handle communication with DToolServer.
 */
public class DeeEngineClient extends EngineClient {
	
	public static DeeEngineClient getDefault() {
		return DeeCore.getDToolClient();
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
				DeeCore.logError(message, throwable);
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
		
		public WorkingCopyStructureUpdateTask(StructureInfo structureInfo, Location fileLocation) {
			super(structureInfo);
			this.fileLocation = fileLocation;
		}
		
		@Override
		protected SourceFileStructure createSourceFileStructure() {
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
	protected StructureUpdateTask createUpdateTask2(StructureInfo structureInfo, String source, 
			Location fileLocation) {
		
		assertNotNull(source);
		
		return new WorkingCopyStructureUpdateTask(structureInfo, fileLocation) {
			
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
	protected StructureUpdateTask createDisposeTask2(StructureInfo structureInfo, Location fileLocation) {
		if(fileLocation == null) {
			return null;
		}
		
		return new WorkingCopyStructureUpdateTask(structureInfo, fileLocation) {
			
			@Override
			protected ParsedModule parseModuleWithNoLocation() {
				return null;
			}
			
			@Override
			protected void modifyWorkingSource(CachedModuleEntry lockedEntry) {
				getParseCache().discardWorkingCopy(fileLocation.toPath());
			}
			
			@Override
			protected ParsedModule parseModuleFromWorkingCopy(CachedModuleEntry entry) {
				return null;
			}
			
		};
	}
	
	/* ----------------- operations ----------------- */
	
	// Only tests may modify this variable:
	public static volatile Location compilerPathOverride = null;
	
	public class CodeCompletionOperation extends EngineOperation<DeeSymbolCompletionResult> {
		
		public CodeCompletionOperation(Location location, int timeoutMillis, int offset) {
			super(DeeEngineClient.this, location, offset, timeoutMillis, "Code Completion");
		}
		
		@Override
		protected DeeSymbolCompletionResult doRunOperationWithWorkingCopy(IProgressMonitor pm) 
				throws CommonException, CoreException, OperationCancellation {
			return dtoolServer.doCodeCompletion(location.toPath(), offset, DeeEngineClient.compilerPathOverride, 
				DeeCorePreferences.getEffectiveDubPath());
		}
		
	}
	
	public class FindDefinitionOperation extends EngineOperation<FindDefinitionResult> {
		
		public FindDefinitionOperation(Location location, int offset, int timeoutMillis) {
			super(DeeEngineClient.this, location, offset, timeoutMillis, "Find Definition");
		}
		
		@Override
		protected FindDefinitionResult doRunOperationWithWorkingCopy(IProgressMonitor pm) 
				throws CommonException, CoreException, OperationCancellation {
			return dtoolServer.doFindDefinition(location.toPath(), offset, DeeCorePreferences.getEffectiveDubPath());
		}
	}
	
	public class FindDDocViewOperation extends EngineOperation<String> {
		
		public FindDDocViewOperation(Location location, int offset, int timeoutMillis) {
			super(DeeEngineClient.this, location, offset, timeoutMillis, "Resolve DDoc");
		}
		
		@Override
		protected String doRunOperationWithWorkingCopy(IProgressMonitor pm) 
				throws CommonException, CoreException, OperationCancellation {
			return dtoolServer.getDDocHTMLView(location.toPath(), offset, DeeCorePreferences.getEffectiveDubPath());
		}
	}
	
}