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
import dtool.dub.DubBundle.DubBundleException;

/**
 * A resolved DUB bundle description. 
 * This is usually derived from running the DUB describe tool, and as such it can be incomplete and have errors.  
 */
public class DubBundleDescription {
	
	protected final boolean isResolved;
	protected final DubBundle mainDubBundle;
	protected final DubBundle[] bundleDependencies;
	protected final DubBundleException error;
	
	/** Constructor for unresolved descriptions. */
	public DubBundleDescription(DubBundle unresolvedBundle) {
		this(unresolvedBundle, EMTPY_BUNDLE_DEPS, false);
	}
	
	public DubBundleDescription(DubBundle mainDubBunble, DubBundle[] deps) {
		this(mainDubBunble, deps, true);
	}
	
	protected DubBundleDescription(DubBundle mainDubBunble, DubBundle[] deps, boolean isResolvedFlag) {
		assertNotNull(mainDubBunble);
		assertNotNull(deps);
		
		this.mainDubBundle = mainDubBunble;
		this.bundleDependencies = deps;
		
		this.error = findError(mainDubBunble, bundleDependencies);
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
		return assertNotNull(bundleDependencies);
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
	
}