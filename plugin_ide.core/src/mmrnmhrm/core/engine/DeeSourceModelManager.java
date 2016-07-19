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

import dtool.engine.ModuleParseCache;
import dtool.engine.ModuleParseCache.CachedModuleEntry;
import dtool.engine.SemanticManager;
import dtool.parser.DeeParserResult.ParsedModule;
import dtool.parser.structure.DeeStructureCreator;
import melnorme.lang.ide.core.engine.SourceModelManager;
import melnorme.lang.tooling.structure.SourceFileStructure;
import melnorme.utilbox.concurrency.OperationCancellation;
import melnorme.utilbox.misc.Location;

public class DeeSourceModelManager extends SourceModelManager {
	
	protected final DeeLanguageEngine languageEngine;
	
	public DeeSourceModelManager() {
		this(DeeLanguageEngine.getDefault());
	}
	
	public DeeSourceModelManager(DeeLanguageEngine languageEngine) {
		super();
		this.languageEngine = assertNotNull(languageEngine);
	}
	
	public SemanticManager getServerSemanticManager() {
		return languageEngine.dtoolServer.getSemanticManager();
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
		protected SourceFileStructure doCreateNewData() throws OperationCancellation {
			ParsedModule parsedModule = (fileLocation == null) ? 
					parseModuleWithNoLocation() :
					setWorkingSourceAndParseModule(fileLocation);
			
			if(parsedModule == null || isCancelled()) {
				throw new OperationCancellation();
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
	
}