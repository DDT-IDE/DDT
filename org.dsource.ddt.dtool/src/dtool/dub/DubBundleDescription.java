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

import java.util.HashMap;

import dtool.dub.DubBundle.DubBundleException;

/**
 * A resolved DUB bundle description. 
 * This is usually derived from running the DUB describe tool, and as such it can be incomplete and have errors.  
 */
public class DubBundleDescription {
	
	protected final boolean isResolved;
	protected final DubBundle mainDubBundle;
	protected final DubBundle[] bundleDependencies; //not null
	protected final DubBundleException error;
	
	/** Constructor for unresolved descriptions. */
	public DubBundleDescription(DubBundle unresolvedBundle) {
		this(unresolvedBundle, EMTPY_BUNDLE_DEPS, false, unresolvedBundle.error);
	}
	
	public DubBundleDescription(DubBundle unresolvedBundle, DubBundleException error) {
		this(unresolvedBundle, EMTPY_BUNDLE_DEPS, false, error);
	}
	
	public DubBundleDescription(DubBundle mainBundle, DubBundle[] bundleDeps) {
		this(mainBundle, bundleDeps, true, findError(mainBundle, bundleDeps));
	}
	
	protected DubBundleDescription(DubBundle mainBundle, DubBundle[] bundleDeps, boolean isResolvedFlag,
			DubBundleException error) {
		
		this.mainDubBundle = assertNotNull(mainBundle);
		this.bundleDependencies = assertNotNull(bundleDeps);
		
		this.error = error;
		this.isResolved = isResolvedFlag && error == null;
	}
	
	protected static DubBundleException findError(DubBundle mainDubBunble, DubBundle[] bundleDependencies) {
		if(mainDubBunble.error != null) {
			return mainDubBunble.error;
		} else {
			for (DubBundle dubBundle : bundleDependencies) {
				if(dubBundle.error != null) {
					return dubBundle.error;
				}
			}
			return null;
		}
	}
	
	protected static final DubBundle[] EMTPY_BUNDLE_DEPS = { };
	
	public DubBundle getMainBundle() {
		return mainDubBundle;
	}
	
	public DubBundle[] getBundleDependencies() {
		return bundleDependencies;
	}
	
	/** A bundle description is considered resolved if dub.json had no errors, and if 
	 * a 'dub describe' output was processed successfully. */
	public boolean isResolved() {
		return isResolved;
	}
	
	public boolean hasErrors() {
		return error != null;
	}
	
	public DubBundleException getError() {
		return error;
	}
	
	// TODO test, make permanent, etc. /*BUG here*/ validate, etc.
	public HashMap<String, BundlePath> getDepBundleToPathMapping() {
		HashMap<String, BundlePath> hashMap = new HashMap<>(bundleDependencies.length);
		
		for (DubBundle bundleDep : bundleDependencies) {
			hashMap.put(bundleDep.getBundleName(), bundleDep.getBundlePath());
		}
		
		// TODO validate cycles.
		
		return hashMap;
	}
	
}