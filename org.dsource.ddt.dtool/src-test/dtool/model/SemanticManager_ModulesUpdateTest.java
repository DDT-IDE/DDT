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
import java.nio.file.Path;

import melnorme.utilbox.misc.FileUtil;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import dtool.dub.CommonDubTest;

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
		 
		// Test add module file of import folder
		 Path writeFile = writeFile(BASIC_LIB.resolve("newModule.d")); 
		 checkIsStale(BASIC_LIB, false);
		 
		 deleteFile(writeFile);
		 checkIsStale(BASIC_LIB, false);
		 
		 
		 // Test module file add
		 Path newModule = writeFile(BASIC_LIB.resolve("source/newModule.d"));
		 checkIsStale(BASIC_LIB, true);
		 checkIsStale(COMPLEX_LIB, StaleState.DEPS_ONLY);
		 checkHasModule(complexLibSR, "newModule", false);
		 complexLibSR = testGetUpdatedResolution(COMPLEX_LIB);
		 checkIsStale(BASIC_LIB, false);
		 checkHasModule(complexLibSR, "newModule", true);
		 
		// Test module file delete
		 deleteFile(newModule);
		 checkIsStale(BASIC_LIB, true);
		 checkIsStale(COMPLEX_LIB, StaleState.DEPS_ONLY);
		 complexLibSR = testGetUpdatedResolution(COMPLEX_LIB);
		 checkHasModule(complexLibSR, "newModule", false);
	}
	
	protected Path writeFile(Path filePath) {
		Path newFile = filePath;
		writeStringToFile(newFile, "int foo;");
		sm.reportFileChange(newFile);
		return newFile;
	}
	
	protected void deleteFile(Path file) throws IOException {
		assertTrue(FileUtil.deleteIfExists(file));
		sm.reportFileChange(file);
		assertTrue(file.toFile().exists() == false);
	}
	
}