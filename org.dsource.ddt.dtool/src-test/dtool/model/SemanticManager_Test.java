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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertEquals;

import java.nio.file.Path;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import dtool.dub.CommonDubTest;
import dtool.model.SemanticManager.SemanticContext;
import dtool.tests.DToolTestResources;

public class SemanticManager_Test {
	
	public static final Path SEMMODEL_TEST_BUNDLES = DToolTestResources.getTestResourcePath("semanticModel");
	
	public static final Path BASIC_BUNDLE_PATH = SEMMODEL_TEST_BUNDLES.resolve("basic_lib_foo");
	public static final Path FOO_LIB_BUNDLE_PATH = SEMMODEL_TEST_BUNDLES.resolve("sm_test_foo");
	
	
	@BeforeClass
	public static void initDubRepositoriesPath() {
		CommonDubTest.dubAddPath(SEMMODEL_TEST_BUNDLES);
	}
	@AfterClass
	public static void cleanupDubRepositoriesPath() {
		CommonDubTest.dubRemovePath(SEMMODEL_TEST_BUNDLES);
	}
	
	@Test
	public void testname() throws Exception { testname$(); }
	public void testname$() throws Exception {
		
		SemanticManager mgr = new SemanticManager(DToolServer.getProcessAgent());
		
		SemanticContext semanticContext = mgr.getSemanticContext(BASIC_BUNDLE_PATH);
		assertEquals(semanticContext.getBundleId(), "basic_lib_foo");
		
		
		semanticContext = mgr.getSemanticContext(FOO_LIB_BUNDLE_PATH);
		assertEquals(semanticContext.getBundleId(), "sm_test_foo");
		
		// TODO: test caching
	}
}