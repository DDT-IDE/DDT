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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.CoreUtil.areEqual;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;

import melnorme.lang.tooling.context.ModuleSourceException;
import melnorme.lang.tooling.ops.FileModificationDetectionHelper;
import melnorme.utilbox.misc.FileUtil;
import melnorme.utilbox.misc.Location;
import melnorme.utilbox.misc.StringUtil;
import dtool.ast.definitions.Module;
import dtool.parser.DeeParser;
import dtool.parser.DeeParserResult.ParsedModule;

/**
 * Manages a cache of parsed modules, indexed by file path
 */
public class ModuleParseCache {
	
	protected final DToolServer dtoolServer;
	
	public ModuleParseCache(DToolServer dtoolServer) {
		this.dtoolServer = dtoolServer;
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
	
	public ParsedModule setWorkingCopyAndGetParsedModule(Path filePath, String source) {
		return parseModuleWithNewSource(filePath, source);
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
				entry = new CachedModuleEntry_Logged(filePath);
				cache.put(key, entry);
			}
			return entry;
		}
	}
	
	protected ParsedModule parseModuleWithNewSource(Path filePath, String source) {
		assertNotNull(source);
		
		return getEntry(filePath).getParsedModuleWithWorkingCopySource(source);
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
		
		public synchronized ParsedModule getParsedModule() throws FileNotFoundException, IOException {
			if(isStale()) {
				readSource();
			}			
			return doGetParsedModule(source);
		}
		
		protected void readSource() throws IOException, FileNotFoundException {
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
		
		public synchronized ParsedModule getParsedModuleWithWorkingCopySource(String newSource) {
			assertNotNull(newSource);
			setNewSource(newSource);
			isWorkingCopy = true;
			return doGetParsedModule(newSource);
		}
		
		protected ParsedModule doGetParsedModule(String source) {
			if(parsedModule == null) {
				parsedModule = DeeParser.parseSource(source, filePath);
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
		
	}
	
	public class CachedModuleEntry_Logged extends CachedModuleEntry {
		
		public CachedModuleEntry_Logged(Path filePath) {
			super(filePath);
		}
		
		@Override
		protected void parsedSource_after() {
			String isWorkingCopySuffix = isWorkingCopy() ? " [WorkingCopy]" : "";
			dtoolServer.logMessage("ParseCache: Parsed module " + filePath + isWorkingCopySuffix);
		}
		
		@Override
		protected void discardWorkingCopy_after() {
			dtoolServer.logMessage("ParseCache: Discarded working copy: " + filePath);
		}
	}
	
}