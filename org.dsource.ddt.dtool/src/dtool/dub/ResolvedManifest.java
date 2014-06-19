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

import java.util.Collections;
import java.util.List;

/**
 * A {@link ResolvedManifest} has manifest info for a bundle, 
 * plus the same info for all dependencies, organized into a tree.
 */
public class ResolvedManifest {
	
	public final DubBundle bundle;
	public final BundlePath bundlePath;
	protected final List<ResolvedManifest> bundleDependencies;
	
	public ResolvedManifest(DubBundle bundle, BundlePath bundlePath, List<ResolvedManifest> bundleDependencies) {
		this.bundle = bundle;
		this.bundlePath = bundlePath;
		this.bundleDependencies = Collections.unmodifiableList(bundleDependencies);
	}
	
	public DubBundle getBundle() {
		return bundle;
	}
	
	public String getBundleName() {
		return bundle.getBundleName();
	}
	
	public BundlePath getBundlePath() {
		return bundlePath;
	}
	
	public List<ResolvedManifest> getBundleDeps() {
		return bundleDependencies;
	}
	
}