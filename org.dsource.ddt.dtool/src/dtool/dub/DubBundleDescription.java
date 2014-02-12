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
package dtool.dub;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.Arrays;

import dtool.dub.DubBundle.DubBundleException;

/**
 * A resolved DUB bundle description. 
 * This is usually derived from running the DUB describe tool, and as such it can be incomplete and have errors.  
 */
public class DubBundleDescription {
	
	protected static final DubBundle[] EMTPY_BUNDLE_DEPS = { };
	
	protected final String bundleName;
	protected final DubBundleException error;
	protected final DubBundle mainDubBundle;
	protected final DubBundle[] bundleDependencies;
	
	public DubBundleDescription(String bundleName, DubBundle[] bundles, DubBundleException error) {
		this.bundleName = bundleName;
		this.error = error;
		
		if(bundles != null && bundles.length >= 1) {
			if(!hasErrors()) {
				// If no main error, then bundles must have no errors as well
				for (DubBundle dubBundle : bundles) {
					assertTrue(!dubBundle.hasErrors());
				}
			}
			mainDubBundle = bundles[0];
			bundleDependencies = Arrays.copyOfRange(bundles, 1, bundles.length);
		} else {
			mainDubBundle = null;
			bundleDependencies = EMTPY_BUNDLE_DEPS;
		}
		
		if(!hasErrors()) {
			assertTrue(bundles.length >= 1);
		}
		
	}
	
	public boolean hasErrors() {
		return error != null;
	}
	
	public DubBundle getMainBundle() {
		return mainDubBundle;
	}
	
	public DubBundle[] getBundleDependencies() {
		return assertNotNull(bundleDependencies);
	}
	
	public DubBundleException getError() {
		return error;
	}
	
	public boolean isResolved() {
		return true;
	}
	
}