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
package dtool.engine;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import melnorme.lang.tooling.bundles.IModuleResolver;
import melnorme.lang.tooling.bundles.ModuleFullName;
import dtool.dub.BundlePath;
import dtool.dub.DubBundle;
import dtool.dub.ResolvedManifest;
import dtool.engine.ModuleParseCache.ParseSourceException;

public class BundleResolution extends AbstractBundleResolution implements IModuleResolver {
	
	protected final ResolvedManifest manifest;
	protected final DubBundle dubBundle;
	protected final BundlePath bundlePath;
	protected final StandardLibraryResolution stdLibResolution;
	protected final List<BundleResolution> depResolutions;
	
	public BundleResolution(SemanticManager manager, ResolvedManifest manifest, 
			StandardLibraryResolution stdLibResolution) {
		super(manager, manifest.getBundle().getEffectiveImportFolders_AbsolutePath());
		this.manifest = manifest;
		this.dubBundle = manifest.getBundle();
		this.bundlePath = assertNotNull(dubBundle.getBundlePath());
		this.stdLibResolution = assertNotNull(stdLibResolution); 
		this.depResolutions = Collections.unmodifiableList(createDepSRs(manager, manifest, stdLibResolution));
	}
	
	protected static List<BundleResolution> createDepSRs(SemanticManager manager, ResolvedManifest manifest, 
		StandardLibraryResolution stdLibResolution) {
		List<BundleResolution> depSRs = new ArrayList<>();
		for (ResolvedManifest depManifest : manifest.getBundleDeps()) {
			depSRs.add(new BundleResolution(manager, depManifest, stdLibResolution));
		}
		return depSRs;
	}
	
	public String getBundleName() {
		return dubBundle.getBundleName();
	}
	
	public BundlePath getBundlePath() {
		return bundlePath;
	}
	
	public DubBundle getBundle() {
		return dubBundle;
	}
	
	public List<BundleResolution> getDirectDependencies() {
		return depResolutions;
	}
	
	public StandardLibraryResolution getStdLibResolution() {
		return stdLibResolution;
	}
	
	public Path getCompilerPath() {
		return getStdLibResolution().getCompilerInstall().getCompilerPath();
	}
	
	@Override
	public String toString() {
		return "BundleResolution: " + getBundleName() + " - " + getBundlePath();
	}
	
	// As an optimization, we don't check STD_LIB staleness, as its likely to change very rarely.
	protected static boolean CHECK_STD_LIB_STALENESS = false;
	
	@Override
	public boolean checkIsStale() {
		if(checkIsModuleListStale() || checkIsModuleContentsStale()) {
			return true;
		}
		
		if(CHECK_STD_LIB_STALENESS && stdLibResolution.checkIsStale()) {
			return true;
		}
		
		for (BundleResolution bundleRes : depResolutions) {
			if(bundleRes.checkIsStale()) {
				return true;
			}
		}
		return false;
	}
	
	/* ----------------- ----------------- */
	
	@Override
	protected void findModules(String fullNamePrefix, HashSet<String> matchedModules) {
		stdLibResolution.findModules(fullNamePrefix, matchedModules);
		
		bundleModules.findModules(fullNamePrefix, matchedModules);
		for (BundleResolution depSR : depResolutions) {
			depSR.findModules(fullNamePrefix, matchedModules);
		}
	}
	
	@Override
	public ResolvedModule findResolvedModule(ModuleFullName moduleFullName) throws ParseSourceException {
		ResolvedModule resolvedModule;
		
		resolvedModule = stdLibResolution.findResolvedModule(moduleFullName);
		if(resolvedModule != null) 
			return resolvedModule;
		
		resolvedModule = getBundleResolvedModule(moduleFullName);
		if(resolvedModule != null) 
			return resolvedModule;
		
		for (BundleResolution depBundleRes : depResolutions) {
			resolvedModule = depBundleRes.findResolvedModule(moduleFullName);
			if(resolvedModule != null) 
				return resolvedModule;
		}
		return null;
	}
	
}