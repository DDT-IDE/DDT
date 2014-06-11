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

import static dtool.model.CommonSemanticModelTest.StaleState.DEPS_ONLY;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import org.junit.Test;

import dtool.dub.BundlePath;
import dtool.dub.ResolvedBundle;

public class SemanticManager_Test extends CommonSemanticModelTest {
	
	protected HashMap<BundlePath, BundleSemanticResolution> previousSRs;
	
	protected ResolvedBundle storeCurrentInMap(BundlePath bundlePath) throws ExecutionException {
		BundleSemanticResolution bundleSR = sm.getStoredResolution(bundlePath);
		previousSRs.put(bundlePath, bundleSR);
		checkChanged(bundlePath, false);
		return bundleSR;
	}
	
	protected void checkChanged(BundlePath bundlePath, boolean expectedChanged) throws ExecutionException {
		ResolvedBundle previousManifest = previousSRs.get(bundlePath);
		if(previousManifest != null) {
			assertTrue(previousManifest.bundlePath.equals(bundlePath));
		}
		boolean changed = previousManifest != sm.getStoredResolution(bundlePath);
		assertTrue(changed == expectedChanged);
		if(expectedChanged) {
			checkIsStale(bundlePath, false);
		}
	}
	
	protected void __storeCurrentManifests__() throws ExecutionException {
		previousSRs = new HashMap<>();
		storeCurrentInMap(BASIC_LIB);
		storeCurrentInMap(BASIC_LIB2);
		storeCurrentInMap(SMTEST);
		storeCurrentInMap(COMPLEX_LIB);
		storeCurrentInMap(COMPLEX_BUNDLE);
	}
	
	/* -----------------  ----------------- */
	
	@Test
	public void testCaching() throws Exception { testCaching$(); }
	public void testCaching$() throws Exception {
		sm = new SemanticManager(new Tests_DToolServer());
		
		__storeCurrentManifests__();
		testGetUpdatedResolution(BASIC_LIB);
		checkChanged(BASIC_LIB, true);
		checkChanged(SMTEST, false);
		assertAreEqual(sm.getStoredResolution(SMTEST), null);
		
		__storeCurrentManifests__();
		testGetUpdatedResolution(SMTEST);
		checkChanged(SMTEST, true);
		checkChanged(BASIC_LIB, true);
		checkChanged(BASIC_LIB2, false);
		
		__storeCurrentManifests__();
		testGetUpdatedResolution(COMPLEX_BUNDLE);
		checkChanged(COMPLEX_BUNDLE, true);
		checkChanged(SMTEST, true);
		checkChanged(BASIC_LIB, true);
		checkChanged(COMPLEX_LIB, true);
		checkChanged(BASIC_LIB2, true);
		
		// Test bundle invalidation
		__storeCurrentManifests__();
		sm.invalidateBundleManifest(BASIC_LIB);
		checkIsStale(BASIC_LIB, true);
		checkIsStale(BASIC_LIB2, false);
		checkIsStale(SMTEST, DEPS_ONLY);
		
		sm.getUpdatedResolution(SMTEST);
		checkChanged(SMTEST, true);
		checkChanged(BASIC_LIB, true);
		checkChanged(BASIC_LIB2, false);
		checkChanged(COMPLEX_BUNDLE, false);
		
		// Test effect of invalidation+update of a dependency bundle in a dependee bundle
		__storeCurrentManifests__();
		sm.invalidateBundleManifest(BASIC_LIB);
		checkIsStale(BASIC_LIB, true);
		checkIsStale(SMTEST, DEPS_ONLY);
		sm.getUpdatedResolution(BASIC_LIB);
		checkChanged(BASIC_LIB, true);
		checkIsStale(SMTEST, DEPS_ONLY);
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
		ResolvedBundle manifest;
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