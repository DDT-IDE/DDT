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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import org.junit.Test;

import dtool.dub.BundlePath;
import dtool.dub.ResolvedManifest;

public class SemanticManager_Test extends CommonSemanticModelTest {
	
	protected SemanticManager sm;
	
	protected HashMap<BundlePath, ResolvedManifest> previousManifests;
	
	protected void __storeCurrentManifests__() throws ExecutionException {
		previousManifests = new HashMap<>();
		storeCurrentInMap(BASIC_LIB);
		storeCurrentInMap(BASIC_LIB2);
		storeCurrentInMap(SMTEST);
		storeCurrentInMap(COMPLEX_LIB);
		storeCurrentInMap(COMPLEX_BUNDLE);
	}
	
	protected ResolvedManifest storeCurrentInMap(BundlePath bundlePath) throws ExecutionException {
		ResolvedManifest manifest = sm.getStoredResolution(bundlePath);
		previousManifests.put(bundlePath, manifest);
		return manifest;
	}
	
	protected void checkChanged(BundlePath bundlePath, boolean expectedChanged) throws ExecutionException {
		ResolvedManifest previousManifest = previousManifests.get(bundlePath);
		if(previousManifest != null) {
			assertTrue(previousManifest.bundlePath.equals(bundlePath));
		}
		boolean changed = previousManifest != sm.getStoredResolution(bundlePath);
		assertTrue(changed == expectedChanged);
	}
	
	@Test
	public void testCaching() throws Exception { testCaching$(); }
	public void testCaching$() throws Exception {
		sm = new SemanticManager(new Tests_DToolServer());
		
		__storeCurrentManifests__();
		checkChanged(BASIC_LIB, false);
		checkChanged(SMTEST, false);
		testGetFullyUpdated(BASIC_LIB);
		checkChanged(BASIC_LIB, true);
		checkChanged(SMTEST, false);
		assertAreEqual(sm.getStoredResolution(SMTEST), null);
		
		__storeCurrentManifests__();
		testGetFullyUpdated(SMTEST);
		checkChanged(SMTEST, true);
		checkChanged(BASIC_LIB, true);
		checkChanged(BASIC_LIB2, false);
		
		__storeCurrentManifests__();
		testGetFullyUpdated(COMPLEX_BUNDLE);
		checkChanged(COMPLEX_BUNDLE, true);
		checkChanged(SMTEST, true);
		checkChanged(BASIC_LIB, true);
		checkChanged(COMPLEX_LIB, true);
		checkChanged(BASIC_LIB2, true);
		
		__storeCurrentManifests__();
		sm.invalidateCurrentManifest(BASIC_LIB);
		checkIsUpdated(BASIC_LIB, false);
		checkIsUpdated(BASIC_LIB2, true);
		checkIsUpdatedForInternallyUpdated(SMTEST);
		
		sm.getUpdatedResolution(SMTEST);
		checkChanged(SMTEST, true);
		checkChanged(BASIC_LIB, true);
		checkChanged(BASIC_LIB2, false);
		checkChanged(COMPLEX_BUNDLE, false);
		
		// Test effect of invalidation+update of a dependency bundle in a dependee bundle
		__storeCurrentManifests__();
		sm.invalidateCurrentManifest(BASIC_LIB);
		checkIsUpdated(BASIC_LIB, false);
		checkIsUpdatedForInternallyUpdated(SMTEST);
		sm.getUpdatedResolution(BASIC_LIB);
		checkChanged(BASIC_LIB, true);
		checkIsUpdatedForInternallyUpdated(SMTEST);
	}
	
	protected void checkIsUpdated(BundlePath bundlePath, boolean expected) {
		assertTrue(sm.isInternallyUpdated(bundlePath) == expected);
		assertTrue(sm.isResolutionUpdated(bundlePath) == expected);
	}
	
	protected void checkIsUpdatedForInternallyUpdated(BundlePath bundlePath) throws ExecutionException {
		assertTrue(sm.isInternallyUpdated(bundlePath) == true);
		assertTrue(sm.isResolutionUpdated(bundlePath) == false);
		
		assertTrue(sm.getStoredResolution(bundlePath) == previousManifests.get(bundlePath));
	}
	
	protected ResolvedManifest testGetFullyUpdated(BundlePath bundlePath) throws ExecutionException {
		ResolvedManifest manifest = sm.getUpdatedResolution(bundlePath);
		assertEquals(manifest.bundlePath, bundlePath);
		checkIsFullyUpdated(manifest);
		return manifest;
	}
	
	protected void checkIsFullyUpdated(ResolvedManifest manifest) throws ExecutionException {
		BundlePath bundlePath = manifest.bundlePath;
		assertTrue(sm.getEntry(bundlePath).isStale() == false);
		checkIsUpdated(bundlePath, true);
		ResolvedManifest manifest2 = sm.getUpdatedResolution(bundlePath);
		assertTrue(manifest == manifest2);
	}
	
	/* -----------------  ----------------- */
	
	public static BundlePath NON_EXISTANT = createBP(SEMMODEL_TEST_BUNDLES, "__NonExistant");
	public static BundlePath ERROR_BUNDLE__MISSING_DEP = createBP(SEMMODEL_TEST_BUNDLES, "ErrorBundle_MissingDep");

	@Test
	public void testErrors() throws Exception { testErrors$(); }
	public void testErrors$() throws IOException {
		sm = new SemanticManager(new Tests_DToolServer());
		
		doTestErrors();
	}
	
	protected void doTestErrors() {
		ResolvedManifest manifest;
		try {
			manifest = sm.getUpdatedResolution(NON_EXISTANT);
		} catch (ExecutionException e) {
			assertTrue(e.getCause() instanceof IOException);
		}
		
		try {
			manifest = sm.getUpdatedResolution(ERROR_BUNDLE__MISSING_DEP);
		} catch (ExecutionException e) {
			throw assertFail();
		}
		assertTrue(manifest != null && manifest.bundle.hasErrors());
	}
	
}