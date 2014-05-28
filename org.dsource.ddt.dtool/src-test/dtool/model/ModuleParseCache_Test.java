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
import melnorme.utilbox.misc.StringUtil;

import org.junit.BeforeClass;
import org.junit.Test;

import dtool.dub.CommonDubTest;
import dtool.model.ModuleParseCache;
import dtool.model.ModuleParseCache.ParseSourceException;
import dtool.parser.DeeParserResult.ParsedModule;
import dtool.tests.DToolTestResources;
import dtool.tests.utils.MiscFileUtils;

public class ModuleParseCache_Test {
	
	public static final Path XPTO_BUNDLE_PATH = CommonDubTest.XPTO_BUNDLE_PATH;
	
	public static final Path WORKING_DIR = DToolTestResources.getWorkingDir().toPath();
	public static final Path TEST_WORKING_DIR = WORKING_DIR.resolve(ModuleParseCache_Test.class.getSimpleName());
	
	protected static final Path CU_PATH = TEST_WORKING_DIR.resolve("app.d");
	protected static final String SOURCE1 = "module app; /** Source 1 */";
	protected static final String WC_SOURCE = "module app; /** ModuleParse Test */";
	protected static final String SOURCE3 = "module app; /** ModuleParse Test source3 */";

	protected ModuleParseCache mpm;
	
	@BeforeClass
	public static void setup() {
		MiscFileUtils.deleteDir(TEST_WORKING_DIR.toFile());
		MiscFileUtils.copyDirContentsIntoDirectory(XPTO_BUNDLE_PATH.resolve("src/"), TEST_WORKING_DIR);
	}
	
	@Test
	public void testCaching() throws Exception { testCaching$(); }
	public void testCaching$() throws Exception {
		mpm = new ModuleParseCache();
		
		basicSequence(CU_PATH);
		// repeat
		basicSequence(CU_PATH);
	}
	
	protected void basicSequence(Path cuPath) throws ParseSourceException, IOException, FileNotFoundException {
		
		ParsedModule parsedModule = mpm.getParsedModule(CU_PATH);
		assertTrue(mpm.getParsedModule(cuPath) == parsedModule);
		
		FileUtil.writeStringToFile(CU_PATH.toFile(), SOURCE1, StringUtil.UTF8);
		assertTrue(mpm.getParsedModule(CU_PATH).source.equals(SOURCE1));
		
		
		// Test caching
		checkGetParsedModule(CU_PATH, WC_SOURCE);
		
		// Test new source update over previous working copy
		checkGetParsedModule(CU_PATH, "blah");
		
		mpm.getParsedModule(CU_PATH, WC_SOURCE);
		FileUtil.writeStringToFile(CU_PATH.toFile(), SOURCE3, StringUtil.UTF8);
		// Check working copy source still takes precedence
		assertTrue(mpm.getParsedModule(CU_PATH).source.equals(WC_SOURCE));
		checkGetParsedModule(CU_PATH, WC_SOURCE);
		
		mpm.discardWorkingCopy(CU_PATH);
		// Test that file now takes precedence.
		assertTrue(mpm.getParsedModule(CU_PATH).source.equals(SOURCE3));
		
		testOptmizationsAfterDiscard();
	}
	
	protected void testOptmizationsAfterDiscard() throws IOException, FileNotFoundException, ParseSourceException {
		mpm.getParsedModule(CU_PATH, "reset");
		ParsedModule parsedModule = mpm.getParsedModule(CU_PATH, WC_SOURCE);
		FileUtil.writeStringToFile(CU_PATH.toFile(), WC_SOURCE, StringUtil.UTF8);
		mpm.discardWorkingCopy(CU_PATH);
		ParsedModule newParsedModule = mpm.getParsedModule(CU_PATH);
		// Test that a reparse did not happen, even though file is newer (but source is same)
		assertTrue(parsedModule == newParsedModule);
		assertTrue(parsedModule.module == newParsedModule.module);
	}
	
	protected void checkGetParsedModule(Path modulePath, String source) throws ParseSourceException {
		ParsedModule parsedModule = mpm.getParsedModule(modulePath, source);
		assertTrue(mpm.getParsedModule(modulePath) == parsedModule);
		assertTrue(mpm.getParsedModule(modulePath, source) == parsedModule);
		assertTrue(parsedModule.source == source);
	}
	
}