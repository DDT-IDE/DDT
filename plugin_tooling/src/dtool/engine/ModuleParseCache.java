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
package dtool.engine;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.CoreUtil.areEqual;
import static melnorme.utilbox.misc.StringUtil.substringUntilMatch;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;

import dtool.ast.definitions.Module;
import dtool.parser.DeeParser;
import dtool.parser.DeeParserResult.ParsedModule;
import melnorme.lang.tooling.context.ModuleSourceException;
import melnorme.lang.utils.FileModificationDetectionHelper;
import melnorme.lang.utils.ISimpleStatusLogger;
import melnorme.utilbox.concurrency.ICancelMonitor;
import melnorme.utilbox.concurrency.OperationCancellation;
import melnorme.utilbox.misc.FileUtil;
import melnorme.utilbox.misc.Location;
import melnorme.utilbox.misc.StringUtil;

/**
 * Manages a cache of parsed modules, indexed by file path
 */
public class ModuleParseCache {
	
	protected final ISimpleStatusLogger statusLogger;
	
	public ModuleParseCache(ISimpleStatusLogger statusLogger) {
		this.statusLogger = statusLogger;
	}
	
	protected final HashMap<String, CachedModuleEntry> cache = new HashMap<>();
	
	/* -----------------  ----------------- */
	
	public ParsedModule getParsedModule(Path filePath) throws ModuleSourceException {
		CachedModuleEntry entry = getEntry(filePath);
		try {
			return assertNotNull(entry.getParsedModule());
		} catch (IOException e) {
			throw new ModuleSourceException(e);
		}
	}
	
	public ParsedModule getExistingParsedModule(Path filePath) {
		// TODO: don't create entry
		return getEntry(filePath).getExistingParsedModule();
	}
	
	// util method
	public Module getExistingParsedModuleNode(Path filePath) {
		ParsedModule parsedModule = getExistingParsedModule(filePath);
		return parsedModule == null ? null : parsedModule.module;
	}
	
	
	
	/* -----------------  ----------------- */
	
	protected String keyFromPath(Path filePath) {
		filePath = validatePath(filePath);
		return getKeyFromPath(filePath);
	}
	
	protected Path validatePath(Path filePath) {
		assertNotNull(filePath);
		//filePath can be relative
		//assertTrue(filePath.isAbsolute());
		assertTrue(filePath.getNameCount() > 0);
		filePath = filePath.normalize();
		return filePath;
	}
	
	protected String getKeyFromPath(Path filePath) {
		return filePath.toString();
	}
	
	public CachedModuleEntry getEntry(Path filePath) {
		String key = keyFromPath(filePath);
		
		synchronized(this) {
			CachedModuleEntry entry = cache.get(key);
			if(entry == null) {
				entry = doCreateEntry(filePath);
				cache.put(key, entry);
			}
			return entry;
		}
	}
	
	protected CachedModuleEntry doCreateEntry(Path filePath) {
		return new CachedModuleEntry_Logged(filePath);
	}
	
	public ParsedModule setSourceAndParseModule(Path filePath, String source) {
		assertNotNull(source);
		return getEntry(filePath).setWorkingSourceAndParse(source);
	}
	
	public void discardWorkingCopy(Path filePath) {
		String key = keyFromPath(filePath);
		CachedModuleEntry entry = cache.get(key);
		if(entry != null) {
			entry.discardWorkingCopy();
		}
	}
	
	/**
	 * XXX: This method is currently only used by tests.
	 * It would need review to make sure it actually works fully outside of that tests scenario, 
	 * especially with regards to concurrency.
	 */
	public void discardEntry(Path filePath) {
		String key = keyFromPath(filePath);
		synchronized(this) {
			cache.remove(key);
		}
	}
	
	public static class CachedModuleEntry {
		
		protected final Path filePath;
		protected final FileModificationDetectionHelper fileModDetectHelper;
		
		private String source = null;
		private boolean isWorkingCopy = false;
		private ParsedModule parsedModule = null;
		
		public CachedModuleEntry(Path filePath) {
			this.filePath = assertNotNull(filePath);
			Location fileLocation = Location.createValidOrNull(filePath);
			fileModDetectHelper = (fileLocation == null) ? null : new FileModificationDetectionHelper(fileLocation);
		}
		
		public Path getFilePath() {
			return filePath;
		}
		
		public synchronized ParsedModule getExistingParsedModule() {
			return parsedModule;
		}
		
		public synchronized boolean isWorkingCopy() {
			return isWorkingCopy;
		}
		
		public synchronized boolean isStale() {
			if(isWorkingCopy) {
				assertNotNull(source);
				return false;
			}
			
			assertNotNull(fileModDetectHelper);
			if(source == null || parsedModule == null) {
				return true;
			}
			
			return fileModDetectHelper.isModifiedSinceLastRead();
		}
		
		public synchronized ParsedModule getParsedModule() throws IOException {
			try {
				return getParsedModule(null);
			} catch(OperationCancellation e) {
				throw assertFail(); // Not possible
			}
		}
		
		public ParsedModule getParsedModule(ICancelMonitor cancelMonitor) throws IOException, OperationCancellation {
			if(isStale()) {
				readSource();
			}			
			return doGetParsedModule(source, cancelMonitor);
		}
		
		protected void readSource() throws IOException {
			fileModDetectHelper.markRead(); // Update timestampe before reading contents.
			String fileContents = FileUtil.readStringFromFile(filePath, StringUtil.UTF8); // TODO: detect encoding
			setNewSource(fileContents);
		}
		
		protected void setNewSource(String newSource) {
			// We only set a new parsedModule if the new source is actually different from previous source.
			// Otherwise, as an optimization, we simply reuse the previous parsedModule
			if(!areEqual(source, newSource)) {
				source = newSource;
				parsedModule = null;
			}
		}
		
		/**
		 * @return the parsed module from this cache, but only if is up-to-date with the underlying source.
		 * If it is not, return null; 
		 * As such, this method will never cause a module to be parsed, any non-null result is a module
		 * that had been parsed already.
		 */
		public synchronized ParsedModule getParsedModuleIfNotStale() {
			return getParsedModuleIfNotStale(true);
		}
		
		protected synchronized ParsedModule getParsedModuleIfNotStale(boolean attemptSourceRefresh) {
			if(!isStale()) {
				return parsedModule;
			}
			
			if(attemptSourceRefresh) {
				// Attemp an optimization, read the new source, and if it is the same as the previous one,
				// then keep the same parsed module.
				try {
					readSource();
					return parsedModule; // parsedModule will remain the same if the source didn't change.
				} catch (IOException e) {
					return null;
				}
			} else {
				return null;
			}
		}
		
		public synchronized ParsedModule setWorkingSourceAndParse(String newSource) {
			assertNotNull(newSource);
			setWorkingSource(newSource);
			return doGetParsedModule(newSource);
		}
		
		public void setWorkingSource(String newSource) {
			setNewSource(newSource);
			isWorkingCopy = true;
		}
		
		protected ParsedModule doGetParsedModule(String source) {
			try {
				return doGetParsedModule(source, null);
			} catch(OperationCancellation e) {
				throw assertFail();
			}
		}
		
		protected ParsedModule doGetParsedModule(String source, ICancelMonitor cancelMonitor) 
				throws OperationCancellation {
			
			if(parsedModule == null) {
				parsedModule = DeeParser.parseSourceModule(source, filePath, cancelMonitor);
				parsedSource_after();
			}
			return parsedModule;
		}
		
		protected void parsedSource_after() {
		}
		
		public synchronized void discardWorkingCopy() {
			if(isWorkingCopy) {
				isWorkingCopy = false;
				fileModDetectHelper.markStale(); // Mark file as modified
				discardWorkingCopy_after();
			}
		}
		
		protected void discardWorkingCopy_after() {
		}
		
		public synchronized void runUnderEntryLock(Runnable runnable) {
			runnable.run();
		}
		
	}
	
	public ParsedModule parseModuleWithNoLocation(String source, ICancelMonitor cm) throws OperationCancellation {
		statusLogger.logMessage("ParseCache: Parsed module with no location: " + substringUntilMatch(source, "\n"));
		return DeeParser.parseUnlocatedModule(source, "_unnamed", cm);
	}
	
	public class CachedModuleEntry_Logged extends CachedModuleEntry {
		
		public CachedModuleEntry_Logged(Path filePath) {
			super(filePath);
		}
		
		@Override
		protected void parsedSource_after() {
			String isWorkingCopySuffix = isWorkingCopy() ? " [WorkingCopy]" : "";
			statusLogger.logMessage("ParseCache: Parsed module " + filePath + isWorkingCopySuffix);
		}
		
		@Override
		protected void discardWorkingCopy_after() {
			statusLogger.logMessage("ParseCache: Discarded working copy: " + filePath);
		}
	}
	
}