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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

import melnorme.utilbox.misc.FileUtil;
import melnorme.utilbox.misc.MiscUtil;
import melnorme.utilbox.tests.TestsWorkingDir;

import org.junit.BeforeClass;
import org.junit.Test;

import dtool.dub.BundlePath;
import dtool.dub.CommonDubTest;
import dtool.model.CommonSemanticModelTest.Tests_DToolServer;
import dtool.model.ModuleParseCache.ParseSourceException;
import dtool.parser.DeeParserResult.ParsedModule;
import dtool.tests.CommonDToolTest;
import dtool.tests.utils.MiscFileUtils;

public class ModuleParseCache_Test extends CommonDToolTest {
	
	public static final BundlePath XPTO_BUNDLE_PATH = CommonDubTest.XPTO_BUNDLE_PATH;
	
	public static final Path WORKING_DIR = TestsWorkingDir.getWorkingDir().toPath();
	public static final Path TEST_WORKING_DIR = WORKING_DIR.resolve(ModuleParseCache_Test.class.getSimpleName());
	
	protected static final Path CU_PATH = TEST_WORKING_DIR.resolve("app.d");
	protected static final String SOURCE1 = "module app; /** Source 1 */";
	protected static final String WC_SOURCE = "module app; /** ModuleParse Test */";
	protected static final String SOURCE3 = "module app; /** ModuleParse Test source3 */";

	protected ModuleParseCache mpm;
	
	@BeforeClass
	public static void setup() throws IOException {
		FileUtil.deleteDir(TEST_WORKING_DIR);
		MiscFileUtils.copyDirContentsIntoDirectory(XPTO_BUNDLE_PATH.resolve("src/"), TEST_WORKING_DIR);
	}
	
	@Test
	public void testCaching() throws Exception { testCaching$(); }
	public void testCaching$() throws Exception {
		mpm = new ModuleParseCache(new Tests_DToolServer());
		
		basicSequence(CU_PATH);
		// repeat
		basicSequence(CU_PATH);
	}
	
	protected void basicSequence(Path cuPath) throws ParseSourceException, IOException, FileNotFoundException {
		
		ParsedModule parsedModule = mpm.getParsedModule(CU_PATH);
		assertTrue(mpm.getParsedModule(cuPath) == parsedModule);
		
		updateFileContents(CU_PATH, SOURCE1);
		assertTrue(mpm.getEntry(CU_PATH).isStale());
		parsedModule = mpm.getParsedModule(CU_PATH);
		assertEquals(parsedModule.source, SOURCE1);
		
		// Test caching
		assertTrue(mpm.getEntry(CU_PATH).isWorkingCopy == false);
		assertTrue(mpm.getEntry(CU_PATH).isStale() == false);
		assertTrue(mpm.getParsedModule(CU_PATH) == parsedModule);
		
		// Test caching for WC
		checkGetParsedModule_ForWorkingCopy(CU_PATH, WC_SOURCE);
		
		// Test new source update over previous working copy
		checkGetParsedModule_ForWorkingCopy(CU_PATH, "blah");
		
		mpm.getParsedModule(CU_PATH, WC_SOURCE);
		updateFileContents(CU_PATH, SOURCE3);
		// Check working copy source still takes precedence
		assertTrue(mpm.getEntry(CU_PATH).isStale() == false);
		assertTrue(mpm.getParsedModule(CU_PATH).source.equals(WC_SOURCE));
		checkGetParsedModule_ForWorkingCopy(CU_PATH, WC_SOURCE);
		
		mpm.discardWorkingCopy(CU_PATH);
		// Test that file now takes precedence.
		assertTrue(mpm.getEntry(CU_PATH).isStale());
		assertTrue(mpm.getParsedModule(CU_PATH).source.equals(SOURCE3));
		
		testOptmizationsAfterDiscard();
	}
	
	public static void updateFileContents(Path filePath, String string) {
		writeStringToFile(filePath, string);
	}
	
	protected void testOptmizationsAfterDiscard() throws IOException, FileNotFoundException, ParseSourceException {
		mpm.getParsedModule(CU_PATH, "___reset___");
		ParsedModule parsedModule = mpm.getParsedModule(CU_PATH, WC_SOURCE);
		updateFileContents(CU_PATH, WC_SOURCE);
		mpm.discardWorkingCopy(CU_PATH);
		
		//assertTrue(mpm.getEntry(CU_PATH).isStale() == true);
		
		ParsedModule newParsedModule = mpm.getParsedModule(CU_PATH);
		// Test that a reparse did not happen, even though file is newer (but source is same)
		assertTrue(parsedModule == newParsedModule);
		assertTrue(parsedModule.module == newParsedModule.module);
	}
	
	protected void checkGetParsedModule_ForWorkingCopy(Path modulePath, String source) throws ParseSourceException {
		ParsedModule parsedModule = mpm.getParsedModule(modulePath, source);
		assertTrue(mpm.getEntry(CU_PATH).isStale() == false);
		assertTrue(mpm.getParsedModule(modulePath) == parsedModule);
		assertTrue(mpm.getParsedModule(modulePath, source) == parsedModule);
		assertTrue(parsedModule.source == source);
	}
	
	@Test
	public void testOther() throws Exception { testOther$(); }
	public void testOther$() throws Exception {
		mpm = new ModuleParseCache(new Tests_DToolServer());
		
		Path freeformPath = MiscUtil.createValidPath("#freeFormPath");
		assertTrue(mpm.getParsedModule(freeformPath, SOURCE1).source.equals(SOURCE1));
	}
	
}