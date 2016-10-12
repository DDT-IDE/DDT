/*******************************************************************************
 * Copyright (c) 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.engine;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.HashSet;

import melnorme.lang.tooling.BundlePath;
import melnorme.lang.tooling.context.BundleModules;
import melnorme.utilbox.collections.Indexable;
import melnorme.utilbox.misc.Location;
import melnorme.utilbox.misc.StringUtil;

public class BundleResolution extends AbstractBundleResolution {
	
	protected final ResolutionKey resKey;
	protected final StandardLibraryResolution stdLibResolution;
	
	protected final Indexable<? extends BundleResolution> depResolutions;
	
	public BundleResolution(SemanticManager manager, BundleKey bundleKey, BundleModules bundleModules,
			StandardLibraryResolution stdLibResolution, Indexable<? extends BundleResolution> depResolutions) {
		super(manager, bundleModules);
		this.stdLibResolution = assertNotNull(stdLibResolution);
		this.resKey = bundleKey == null ? null :  
				new ResolutionKey(bundleKey, getStdLibResolution().getCompilerInstall());
		
		this.depResolutions = depResolutions;
	}
	
	public BundleKey getBundleKey() {
		return resKey == null ? null : resKey.bundleKey;
	}
	
	public ResolutionKey getResKey() {
		return resKey;
	}
	
	public BundlePath getBundlePath() {
		return getBundleKey() != null ? getBundleKey().bundlePath : null;
	}
	
	public Indexable<? extends BundleResolution> getDirectDependencies() {
		return depResolutions;
	}
	
	@Override
	public StandardLibraryResolution getStdLibResolution() {
		return stdLibResolution;
	}
	
	public Location getCompilerPath() {
		return getStdLibResolution().getCompilerInstall().getCompilerPath();
	}
	
	@Override
	public String toString() {
		if(resKey == null) {
			return "BundleResolution: [" + StringUtil.collToString(bundleModules.moduleFiles, ":") + "]";
		}
		return "BundleResolution: " + resKey;
	}

	
	/* -----------------  ----------------- */
	
	@Override
	public boolean checkIsStale() {
		HashSet<BundleKey> verifiedBundles = new HashSet<>();
		return checkIsStale(true, verifiedBundles);
	}
	
	public boolean checkIsStale(boolean checkStdLib, HashSet<BundleKey> verifiedBundles) {
		
		if(checkIsModuleListStale() || checkIsModuleContentsStale()) {
			return true;
		}
		
		if(checkStdLib) {
			if(stdLibResolution.checkIsStale()) {
				return true;
			}
		}
		
		// Mark this bundle as having the staleness verified, 
		// this an optimization to avoid exponential growth of duplicate bundle checks for projects
		// with complex dependency trees (ie, vibe for example).
		verifiedBundles.add(getBundleKey());
		
		for (BundleResolution bundleRes : depResolutions) {
			
			if(verifiedBundles.contains(bundleRes.getBundleKey())) {
				continue;
			}
			
			if(bundleRes.checkIsStale(false, verifiedBundles)) {
				return true;
			}
		}
		return false;
	}
	
	/* ----------------- ----------------- */
	
	@Override
	public <E extends Exception> void visitBundleResolutionsAfterStdLib(BundleResolutionVisitor<?, E> visitor) 
			throws E {
		visitor.visit(this);
		if(visitor.isFinished()) {
			return;
		}
		
		for (BundleResolution depBundleRes : depResolutions) {
			depBundleRes.visitBundleResolutionsAfterStdLib(visitor);
			if(visitor.isFinished()) {
				return;
			}
		}
	}
	
}