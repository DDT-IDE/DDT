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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;

import melnorme.utilbox.misc.FileUtil;
import melnorme.utilbox.misc.MiscUtil;
import melnorme.utilbox.tests.TestsWorkingDir;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import dtool.dub.BundlePath;
import dtool.dub.CommonDubTest;
import dtool.model.BundleSemanticResolution.ResolvedModule;

public class SemanticManager_ModulesUpdateTest extends CommonSemanticModelTest {
	
	@BeforeClass
	public static void initDubRepositoriesPath() {
		CommonDubTest.dubRemovePath(SEMMODEL_TEST_BUNDLES);
		CommonDubTest.dubAddPath(WORKING_DIR);
	}
	
	@AfterClass
	public static void cleanupDubRepositoriesPath() {
		CommonDubTest.dubRemovePath(WORKING_DIR);
	}
	
	@Override
	public Path getDubRepositoryDir() {
		return WORKING_DIR;
	}
	
	@Test
	public void testUpdates() throws Exception { testUpdates$(); }
	public void testUpdates$() throws Exception {
		prepSMTestsWorkingDir();
		__initSemanticManager();
		 
		BundleSemanticResolution complexLibSR = testGetUpdatedResolution(COMPLEX_LIB);
		
		// Test file that belongs to no bundle : no effect.
		sm.reportFileChange(TestsWorkingDir.getWorkingDir().toPath().resolve(".."));
		// Test non-absolute path: no effect.
		sm.reportFileChange(MiscUtil.createPath("blah"));
		
		// Test add module file outside of import folder
		testNoEffectFileChange(BASIC_LIB, BASIC_LIB.resolve("newModule.d"));
		// Test invalid module file name
		testNoEffectFileChange(BASIC_LIB, BASIC_LIB.resolve("source/new-Module.d"));
		testNoEffectFileChange(BASIC_LIB, BASIC_LIB.resolve("source/pack.blah/newModule.d"));
		// Some corner cases:
		testNoEffectFileChange(BASIC_LIB, BASIC_LIB.resolve("source"));
		testNoEffectFileChange(BASIC_LIB, BASIC_LIB.path);

		
		 // Test module file add
		Path newModule = writeFile(BASIC_LIB.resolve("source/newModule.d"));
		checkIsStale(BASIC_LIB, StaleState.MODULE_LIST_STALE);
		checkIsStaleInDepsOnly(COMPLEX_LIB);
		checkHasModule(complexLibSR, "newModule", false);
		complexLibSR = testGetUpdatedResolution(COMPLEX_LIB);
		checkIsStale(BASIC_LIB, false);
		checkHasModule(complexLibSR, "newModule", true);
		 
		// Test module file delete
		deleteFile(newModule);
		checkIsStale(BASIC_LIB, StaleState.MODULE_LIST_STALE);
		checkIsStaleInDepsOnly(COMPLEX_LIB);
		complexLibSR = testGetUpdatedResolution(COMPLEX_LIB);
		checkHasModule(complexLibSR, "newModule", false);
		 
		// Test module file modification
		Path module = writeFile(BASIC_LIB.resolve("source/basic_lib_pack/foo.d"));
		checkIsStale(BASIC_LIB, StaleState.MODULE_CONTENTS_STALE);
		checkIsStaleInDepsOnly(COMPLEX_LIB);
		
		deleteFile(module);
		checkIsStale(BASIC_LIB, StaleState.MODULE_LIST_STALE);
		checkIsStaleInDepsOnly(COMPLEX_LIB);
		checkHasModule(testGetUpdatedResolution(COMPLEX_LIB), "basic_lib_pack.foo", false);
		
	}
	
	protected void testNoEffectFileChange(BundlePath bundlePath, Path filePath) throws IOException {
		if(filePath.toFile().isDirectory()) {
			sm.reportFileChange(filePath);
			checkIsStale(bundlePath, false);
			return;
		}
		
		writeFile(filePath); 
		checkIsStale(bundlePath, false);
		deleteFile(filePath);
		checkIsStale(bundlePath, false);
	}
	
	protected Path writeFile(Path newFile) throws IOException {
		Files.createDirectories(newFile.getParent());
		writeStringToFile(newFile, "int foo;");
		sm.reportFileChange(newFile);
		return newFile;
	}
	
	protected void deleteFile(Path file) throws IOException {
		assertTrue(FileUtil.deleteIfExists(file));
		sm.reportFileChange(file);
		assertTrue(file.toFile().exists() == false);
	}
	
	protected static final String SOURCE1 = "module source_change1;";
	protected static final String SOURCE2 = "module change2;";
	
	@Test
	public void testModules() throws Exception { testModules$(); }
	public void testModules$() throws ExecutionException {
		sm = new SemanticManager(new Tests_DToolServer());
		
		sm.getUpdatedResolution(COMPLEX_LIB);
		Path complexLib_module = COMPLEX_LIB.path.resolve("source/complex_lib.d");
		
		ResolvedModule semModule = sm.getResolvedModule(complexLib_module);
		assertEquals(semModule.getModuleNode().getName(), "complex_lib");
		
		assertTrue(semModule == sm.getResolvedModule(complexLib_module)); // Check instance remains same.
		
		sm.reportFileChange(complexLib_module);
		
		// Check new instance returned.
		assertTrue(semModule != sm.getResolvedModule(complexLib_module));
		
		
		assertEquals(sm.getResolvedModule(complexLib_module).getModuleNode().getName(), "complex_lib");
		// Test updateWorkingCopyAndParse
		sm.updateWorkingCopyAndParse(complexLib_module, SOURCE1);
		assertEquals(sm.getResolvedModule(complexLib_module).getModuleNode().getName(), "source_change1");
		
		sm.updateWorkingCopyAndParse(complexLib_module, SOURCE2);
		assertEquals(sm.getResolvedModule(complexLib_module).getModuleNode().getName(), "change2");
		
		sm.updateWorkingCopyAndParse(complexLib_module, SOURCE2);
		ResolvedModule resolvedModule = sm.getResolvedModule(complexLib_module);
		sm.updateWorkingCopyAndParse(complexLib_module, SOURCE2);
		// Test optimization: resolved module does not change if updated source was the same
		assertTrue(resolvedModule == sm.getResolvedModule(complexLib_module));
	}
	
}