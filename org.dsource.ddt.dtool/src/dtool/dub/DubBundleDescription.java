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
	protected final boolean isDubDescribeError;
	
	public DubBundleDescription(DubBundle mainDubBunble, DubBundle[] deps) {
		assertNotNull(mainDubBunble);
		assertNotNull(deps);
		
		this.mainDubBundle = mainDubBunble;
		this.bundleDependencies = deps;
		this.isResolved = true;
		this.isDubDescribeError = false;
		
		if(mainDubBunble.error != null) {
			error = mainDubBunble.error;
		} else {
			for (DubBundle dubBundle : bundleDependencies) {
				if(dubBundle.error != null) {
					error = dubBundle.error;
					return;
				}
			}
			error = null;
		}
	}
	
	/** Constructor for unresolved descriptions. */
	public DubBundleDescription(DubBundle unresolvedBundle, boolean isResolved, boolean isDubDescribeError) {
		this.mainDubBundle = unresolvedBundle;
		this.bundleDependencies = EMTPY_BUNDLE_DEPS;
		this.isResolved = isResolved;
		this.error = mainDubBundle.error;
		this.isDubDescribeError = isDubDescribeError;
		if(isDubDescribeError) {
			assertNotNull(error);
		}
	}
	
	protected static final DubBundle[] EMTPY_BUNDLE_DEPS = { };
	
	public DubBundle getMainBundle() {
		return mainDubBundle;
	}
	
	public DubBundle[] getBundleDependencies() {
		return assertNotNull(bundleDependencies);
	}
	
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