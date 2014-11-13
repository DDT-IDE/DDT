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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

import melnorme.lang.tooling.bundles.ModuleSourceException;
import melnorme.lang.utils.MiscFileUtils;
import melnorme.utilbox.misc.FileUtil;
import melnorme.utilbox.misc.MiscUtil;
import melnorme.utilbox.tests.TestsWorkingDir;

import org.junit.BeforeClass;
import org.junit.Test;

import dtool.dub.BundlePath;
import dtool.dub.CommonDubTest;
import dtool.engine.ModuleParseCache;
import dtool.engine.CommonSemanticManagerTest.Tests_DToolServer;
import dtool.parser.DeeParserResult.ParsedModule;
import dtool.tests.CommonDToolTest;

public class ModuleParseCache_Test extends CommonDToolTest {
	
	public static final BundlePath XPTO_BUNDLE_PATH = CommonDubTest.XPTO_BUNDLE_PATH;
	
	public static final Path WORKING_DIR = TestsWorkingDir.getWorkingDir().toPath();
	public static final Path TEST_WORKING_DIR = WORKING_DIR.resolve(ModuleParseCache_Test.class.getSimpleName());
	
	protected static final Path CU_PATH = TEST_WORKING_DIR.resolve("app.d");
	protected static final String SOURCE1 = "module app; /** Source 1 */";
	protected static final String WC_SOURCE = "module app; /** WC Source */";
	protected static final String SOURCE3 = "module app; /** ModuleParse Test source3 */";

	protected ModuleParseCache mpc;
	
	@BeforeClass
	public static void setup() throws IOException {
		FileUtil.deleteDir(TEST_WORKING_DIR);
		MiscFileUtils.copyDirContentsIntoDirectory(XPTO_BUNDLE_PATH.resolve("src/"), TEST_WORKING_DIR);
	}
	
	@Test
	public void testCaching() throws Exception { testCaching$(); }
	public void testCaching$() throws Exception {
		__initModuleParseCache();
		
		basicSequence(CU_PATH);
		// repeat
		basicSequence(CU_PATH);
	}
	
	protected void __initModuleParseCache() {
		mpc = new ModuleParseCache(new Tests_DToolServer());
	}
	
	public static void writeToFileAndUpdateMTime(Path file, String contents) throws IOException {
		if(!file.toFile().exists()) {
			writeStringToFile(file, contents);
			return;
		}
		FileTime lastModifiedTime = Files.getLastModifiedTime(file);
		writeStringToFile(file, contents);
		// Make sure last modified time is different from before
		Files.setLastModifiedTime(file, FileTime.fromMillis(lastModifiedTime.toMillis() - 1_000));
	}
	
	public void updateFileContents(Path filePath, String contents) {
		updateFileContents(filePath, contents, true);
	}
	
	public void updateFileContents(Path filePath, String contents, boolean isEntryStale) {
		try {
			writeToFileAndUpdateMTime(filePath, contents);
		} catch (IOException e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
		assertTrue(mpc.getEntry(filePath).isStale() == isEntryStale);
	}
	
	protected void basicSequence(Path cuPath) throws ModuleSourceException, IOException, FileNotFoundException {
		
		ParsedModule parsedModule = mpc.getParsedModule(CU_PATH);
		assertTrue(mpc.getParsedModule(cuPath) == parsedModule);
		
		updateFileContents(CU_PATH, SOURCE1);
		assertTrue(mpc.getEntry(CU_PATH).isStale());
		parsedModule = mpc.getParsedModule(CU_PATH);
		assertEquals(parsedModule.source, SOURCE1);
		
		// Test caching
		assertTrue(mpc.getEntry(CU_PATH).isWorkingCopy == false);
		assertTrue(mpc.getEntry(CU_PATH).isStale() == false);
		assertTrue(mpc.getParsedModule(CU_PATH) == parsedModule);
		
		// Test caching for WC
		testUpdateWorkingCopyAndParse(CU_PATH, WC_SOURCE);
		
		// Test new source update over previous working copy
		testUpdateWorkingCopyAndParse(CU_PATH, "blah");
		
		// Test file update over active working copy. Check working copy source still takes precedence
		testUpdateWorkingCopyAndParse(CU_PATH, WC_SOURCE);
		updateFileContents(CU_PATH, SOURCE3, false); // do file update
		assertTrue(mpc.getParsedModule(CU_PATH).source.equals(WC_SOURCE));
		
		
		// Test discard working copy - last update was on file
		testUpdateWorkingCopyAndParse(CU_PATH, WC_SOURCE);
		updateFileContents(CU_PATH, SOURCE3, false); // last update
		testDiscardWorkingCopy(CU_PATH, WC_SOURCE, SOURCE3);
		
		// Test discard working copy - last update was WC and file was parsed previously
		updateFileContents(CU_PATH, SOURCE3);
		mpc.getParsedModule(CU_PATH);
		testUpdateWorkingCopyAndParse(CU_PATH, WC_SOURCE); // last update
		testDiscardWorkingCopy(CU_PATH, WC_SOURCE, SOURCE3);
		
		// Test redundant discardWorkingCopy
		mpc.discardWorkingCopy(CU_PATH);
		assertTrue(mpc.getEntry(CU_PATH).isStale() == false);
		
		// Special case: first update for path is working copy, then discard
		__initModuleParseCache();
		updateFileContents(CU_PATH, SOURCE1);
		testUpdateWorkingCopyAndParse(CU_PATH, WC_SOURCE);
		testDiscardWorkingCopy(CU_PATH, WC_SOURCE, SOURCE1);
		
		
		testOptimizationsWithIdenticalSource();
		
		// Special case:
		mpc.discardWorkingCopy(CU_PATH);
		updateFileContents(CU_PATH, SOURCE1);
		parsedModule = mpc.getParsedModule(CU_PATH);
		updateFileContents(CU_PATH, SOURCE3, true);
		assertTrue(getParsedModuleIfNotStale(CU_PATH) == null);
		assertTrue(mpc.getEntry(CU_PATH).isStale());
		assertTrue(mpc.getParsedModule(CU_PATH) != parsedModule);
	}
	
	protected void testUpdateWorkingCopyAndParse(Path modulePath, String source) throws ModuleSourceException {
		ParsedModule parsedModule = mpc.setWorkingCopyAndGetParsedModule(modulePath, source);
		assertTrue(mpc.getEntry(CU_PATH).isStale() == false);
		assertTrue(mpc.getParsedModule(modulePath) == parsedModule);
		assertTrue(mpc.setWorkingCopyAndGetParsedModule(modulePath, source) == parsedModule);
		assertTrue(parsedModule.source == source);
	}
	
	protected void testDiscardWorkingCopy(Path filePath, String previousSource, String expectedNewSource) 
			throws ModuleSourceException {
		assertTrue(mpc.getExistingParsedModule(CU_PATH).source.equals(previousSource));
		mpc.discardWorkingCopy(filePath);
		assertTrue(mpc.getEntry(filePath).isStale());
		assertTrue(mpc.getParsedModule(filePath).source.equals(expectedNewSource));
	}
	
	protected void testOptimizationsWithIdenticalSource() throws IOException, ModuleSourceException {
		mpc.discardWorkingCopy(CU_PATH);
		ParsedModule parsedModule;
		
		updateFileContents(CU_PATH, WC_SOURCE);
		parsedModule = mpc.getParsedModule(CU_PATH);
		updateFileContents(CU_PATH, WC_SOURCE, true);
		testNoReparseHappened(parsedModule, CU_PATH);
		
		assertTrue(parsedModule == setWorkingCopySourceAndParse(CU_PATH, WC_SOURCE));
		testNoReparseHappened(parsedModule, CU_PATH);
		
		parsedModule = setWorkingCopySourceAndParse(CU_PATH, SOURCE3);
		updateFileContents(CU_PATH, SOURCE3, false);
		mpc.discardWorkingCopy(CU_PATH);
		testNoReparseHappened(parsedModule, CU_PATH);
	}
	
	protected void testNoReparseHappened(ParsedModule previousModule, Path filePath) throws ModuleSourceException {
		assertTrue(previousModule == getParsedModuleIfNotStale(filePath));
		assertTrue(mpc.getEntry(filePath).isStale() == false);
		assertTrue(previousModule == mpc.getParsedModule(filePath));
	}
	
	protected ParsedModule getParsedModuleIfNotStale(Path path) {
		return mpc.getEntry(path).getParsedModuleIfNotStale(true);
	}
	
	protected ParsedModule setWorkingCopySourceAndParse(Path filePath, String source) {
		ParsedModule parsedModule = mpc.setWorkingCopyAndGetParsedModule(filePath, source);
		assertTrue(mpc.getEntry(filePath).isWorkingCopy);
		return parsedModule;
	}
	
	@Test
	public void testOther() throws Exception { testOther$(); }
	public void testOther$() throws Exception {
		__initModuleParseCache();
		
		Path freeformPath = MiscUtil.createValidPath("#freeFormPath");
		assertTrue(mpc.setWorkingCopyAndGetParsedModule(freeformPath, SOURCE1).source.equals(SOURCE1));
	}
	
}