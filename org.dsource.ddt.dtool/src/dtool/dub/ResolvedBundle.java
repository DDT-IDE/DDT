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

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import dtool.model.ModuleFullName;

/**
 * This contains holds information analyzed from a bundle manifest file.
 */
public class ResolvedBundle {
	
	public final DubBundle bundle;
	public final BundlePath bundlePath;
	protected final List<BundlePath> bundleDependencies;
	protected final Map<ModuleFullName, Path> bundleModules;
	
	public ResolvedBundle(DubBundle bundle, List<BundlePath> depBundlePaths, 
			Map<ModuleFullName, Path> bundleModules) {
		this.bundle = bundle;
		this.bundlePath = assertNotNull(bundle.getBundlePath());
		this.bundleDependencies = Collections.unmodifiableList(depBundlePaths);
		this.bundleModules = Collections.unmodifiableMap(bundleModules);
	}
	
	public String getBundleName() {
		return bundle.getBundleName();
	}
	
	public BundlePath getBundlePath() {
		return bundlePath;
	}
	
	public List<BundlePath> getBundleDeps() {
		return bundleDependencies;
	}
	
	public Map<ModuleFullName, Path> getBundleModuleFiles() {
		return bundleModules;
	}
	
}