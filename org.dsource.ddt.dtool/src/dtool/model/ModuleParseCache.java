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
package dtool.model;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.CoreUtil.areEqual;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;

import melnorme.utilbox.misc.FileUtil;
import melnorme.utilbox.misc.SimpleLogger;
import melnorme.utilbox.misc.StringUtil;
import dtool.parser.DeeParser;
import dtool.parser.DeeParserResult.ParsedModule;

/**
 * Manages a cache of parsed modules, indexed by file path
 */
public class ModuleParseCache {
	
	protected static SimpleLogger log = SimpleLogger.create("ModuleParseCache");
	
	protected final HashMap<String, ModuleEntry> cache = new HashMap<>();
	
	/* -----------------  ----------------- */
	
	public ParsedModule getParsedModule(Path filePath) throws ParseSourceException {
		ModuleEntry entry = getEntry(filePath);
		try {
			return assertNotNull(entry.getParsedModule());
		} catch (IOException e) {
			throw new ParseSourceException(e);
		}
	}
	
	public ParsedModule getExistingParsedModule(Path filePath) {
		return getEntry(filePath).parsedModule;
	}
	
	public ParsedModule getParsedModule(Path filePath, String source) {
		return parseModuleWithNewSource(filePath, source);
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
	
	protected ModuleEntry getEntry(Path filePath) {
		filePath = validatePath(filePath);
		String key = getKeyFromPath(filePath);
		
		synchronized(this) {
			ModuleEntry entry = cache.get(key);
			if(entry == null) {
				entry = new ModuleEntry(filePath);
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
		filePath = validatePath(filePath);
		String key = getKeyFromPath(filePath);
		ModuleEntry entry = cache.get(key);
		if(entry != null && entry.isWorkingCopy) {
			entry.isWorkingCopy = false;
			log.println("ParseCache: Discarded working copy: ", filePath);
		}
	}
	
	public static class ModuleEntry {
		
		protected final Path filePath;
		
		protected String source = null;
		protected boolean isWorkingCopy = false;
		protected long readTimestamp;
		protected volatile ParsedModule parsedModule = null;
		
		public ModuleEntry(Path filePath) {
			this.filePath = filePath;
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
			if(isWorkingCopy) {
				assertNotNull(source);
			} else {
				File file = filePath.toFile();
				
				if(source == null) {
					readSource(file);
				} else if(file.lastModified() > readTimestamp){ //BUG here if modified twice in same millisecond  
					readSource(file);
				}
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
				parsedModule = DeeParser.parseSource(source, filePath);
				ModuleParseCache.log.println("ParseCache: Parsed Module ", filePath, " ",
					isWorkingCopy ? "[WC]" : "");
			}
			return parsedModule;
		}
		
	}
	
}