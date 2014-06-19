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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;

import melnorme.utilbox.misc.FileUtil;
import melnorme.utilbox.misc.StringUtil;
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
		if(entry != null) {
			entry.discardWorkingCopy();
		}
	}
	
	public class ModuleEntry {
		
		public static final int FILE_MODIFY_TIME__GRANULARITY_Millis = 1_000_000;
		
		protected final Path filePath;
		
		protected String source = null;
		protected boolean isWorkingCopy = false;
		protected BasicFileAttributes fileSyncAttributes;
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
			if(isStale()) {
				readSource(filePath.toFile());
			}			
			return doGetParseModule(source);
		}
		
		public synchronized ParsedModule getParsedModuleIfNotStale() {
			if(isStale()) {
				return null;
			}			
			return parsedModule;		
		}
		
		protected synchronized boolean isStale() {
			if(isWorkingCopy) {
				assertNotNull(source);
				return false;
			}
			if(source == null) {
				return true;
			}
			
			BasicFileAttributes newAttributes;
			try {
				newAttributes = Files.readAttributes(filePath, BasicFileAttributes.class);
			} catch (IOException e) {
				return true;
			}
			
			return hasBeenModified(fileSyncAttributes, newAttributes);
		}
		
		protected synchronized void discardWorkingCopy() {
			if(isWorkingCopy) {
				isWorkingCopy = false;
				fileSyncAttributes = null;
				dtoolServer.logMessage("ParseCache: Discarded working copy: " + filePath);
			}
		}
		
		
		protected void readSource(File file) throws IOException, FileNotFoundException {
			fileSyncAttributes = Files.readAttributes(filePath, BasicFileAttributes.class);
			
			String fileContents = FileUtil.readStringFromFile(file, StringUtil.UTF8); // TODO: detect encoding
			setNewSource(fileContents);
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
				dtoolServer.logMessage("ParseCache: Parsed module " + filePath +
					(isWorkingCopy ? " [WorkingCopy]" : ""));
			}
			return parsedModule;
		}
		
	}
	
	public static boolean hasBeenModified(BasicFileAttributes originalAttributes, BasicFileAttributes newAttributes) {
		return 
				originalAttributes == null ||
				originalAttributes.lastModifiedTime().toMillis() != newAttributes.lastModifiedTime().toMillis() ||
				originalAttributes.size() != newAttributes.size();
	}
	
}