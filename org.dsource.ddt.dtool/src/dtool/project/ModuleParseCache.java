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
package dtool.project;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.CoreUtil.areEqual;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;

import melnorme.utilbox.misc.FileUtil;
import melnorme.utilbox.misc.MiscUtil;
import melnorme.utilbox.misc.SimpleLogger;
import melnorme.utilbox.misc.StringUtil;
import dtool.parser.DeeParser;
import dtool.parser.DeeParserResult.ParsedModule;

/**
 * Manages a cache of parsed modules, indexed by file path
 */
public class ModuleParseCache {
	
	protected SimpleLogger log = SimpleLogger.create("ModuleParseCache");
	
	protected static final ModuleParseCache defaultInstance = new ModuleParseCache();
	
	public static ModuleParseCache getDefault() {
		return defaultInstance;
	}
	
	protected final ConcurrentHashMap<String, ModuleEntry> cache = new ConcurrentHashMap<>();
	
	/* -----------------  ----------------- */
	
	public ParsedModule getParsedModule(String freeformFilePath, String source) {
		Path path = MiscUtil.createValidPath(freeformFilePath);
		if(path != null && path.isAbsolute()) {
			return getParsedModule(path, source);
		}
		String moduleNameFromFilePath = DeeNamingRules.getModuleNameFromFilePath(freeformFilePath);
		ModuleEntry entry = updateEntry(freeformFilePath, null, moduleNameFromFilePath);
		return entry.getParsedModuleWithWorkingCopySource(source);
	}
	
	public ParsedModule getParsedModule(Path filePath, String source) {
		return parseModuleWithNewSource(filePath, source);
	}
	
	public ParsedModule getParsedModule(Path filePath) throws ParseSourceException {
		try {
			return doGetParseResult(filePath);
		} catch (IOException e) {
			throw new ParseSourceException(e);
		}
	}
	
	public static class ParseSourceException extends Exception {
		
		private static final long serialVersionUID = 1L;
		
		
		public ParseSourceException() {
		}
		
		public ParseSourceException(Throwable cause) {
			super(cause);
		}
	}
	
	/* -----------------  ----------------- */
	
	protected Path validatePath(Path filePath) {
		assertTrue(filePath.isAbsolute());
		assertTrue(filePath.getNameCount() > 0);
		filePath = filePath.normalize();
		return filePath;
	}
	
	protected String getKeyFromPath(Path filePath) {
		return filePath.toUri().toString();
	}
	
	
	protected ParsedModule doGetParseResult(Path filePath) throws FileNotFoundException, IOException {
		return updateEntry(filePath).getParsedModule();
	}
	
	protected ParsedModule parseModuleWithNewSource(Path filePath, String source) {
		assertNotNull(source);
		
		return updateEntry(filePath).getParsedModuleWithWorkingCopySource(source);
	}
	
	protected ModuleEntry updateEntry(Path filePath) {
		assertNotNull(filePath);
		filePath = validatePath(filePath);
		String key = getKeyFromPath(filePath);
		String defaultModuleName = DeeNamingRules.getModuleNameFromFilePath(filePath);
		return updateEntry(key, filePath, defaultModuleName);
	}
	
	protected ModuleEntry updateEntry(String key, Path filePath, String defaultModuleName) {
		ModuleEntry entry = cache.get(key);
		if(entry == null) {
			entry = new ModuleEntry(filePath, defaultModuleName);
			cache.put(key, entry);
		}
		return entry;
	}
	
	public void discardWorkingCopy(Path filePath) {
		filePath = validatePath(filePath);
		String key = getKeyFromPath(filePath);
		ModuleEntry entry = cache.get(key);
		if(entry != null && entry.isWorkingCopy) {
			entry.isWorkingCopy = false;
			log.println("ParseCache: Discarded working copy: ", filePath);
		}
	}
	
	public class ModuleEntry {
		
		protected final Path filePath;
		protected final String defaultModuleName;
		
		protected String source = null;
		protected boolean isWorkingCopy = false;
		protected long readTimestamp;
		protected ParsedModule parsedModule = null;
		
		public ModuleEntry(Path filePath, String defaultModuleName) {
			this.filePath = filePath;
			this.defaultModuleName = defaultModuleName;
			
			assertTrue(filePath != null);
		}
		
		public synchronized ParsedModule getParsedModuleWithWorkingCopySource(String newSource) {
			assertNotNull(newSource);
			if(!newSource.equals(source)) {
				source = newSource;
				parsedModule = null;
				isWorkingCopy = true;
			}
			
			return doGetParseModule(newSource);
		}
		
		public synchronized ParsedModule getParsedModule() throws FileNotFoundException, IOException {
			File file = filePath.toFile();
			if(isWorkingCopy) {
				assertNotNull(source);
			} else if(source == null) {
				readSource(file);
			} else if(file.lastModified() > readTimestamp){
				readSource(file);
			}
			
			return doGetParseModule(source);
		}
		
		protected void readSource(File file) throws IOException, FileNotFoundException {
			readTimestamp = file.lastModified();
			String newSource = FileUtil.readStringFromFile(file, StringUtil.UTF8);
			setNewSource(newSource);
		}
		
		protected void setNewSource(String newSource) {
			if(!areEqual(source, newSource)) {
				source = newSource;
				parsedModule = null;
			}
		}
		
		protected ParsedModule doGetParseModule(String source) {
			if(parsedModule == null) {
				parsedModule = DeeParser.parseSource(source, defaultModuleName);
				log.println("ParseCache: Parsed Module ", filePath, " (", defaultModuleName, ")",
					isWorkingCopy ? "[WC]" : "");
			}
			return parsedModule;
		}
		
	}
	
}