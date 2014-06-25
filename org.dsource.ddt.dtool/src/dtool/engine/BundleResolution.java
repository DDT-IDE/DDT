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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import melnorme.utilbox.misc.ArrayUtil;
import dtool.ast.definitions.Module;
import dtool.dub.BundlePath;
import dtool.dub.DubBundle;
import dtool.dub.ResolvedManifest;
import dtool.engine.ModuleParseCache.ParseSourceException;
import dtool.engine.modules.BundleModulesVisitor;
import dtool.engine.modules.IModuleResolver;
import dtool.engine.modules.ModuleFullName;
import dtool.parser.DeeParserResult.ParsedModule;

public class BundleResolution extends AbstractBundleResolution implements IModuleResolver {
	
	protected final ResolvedManifest manifest;
	protected final DubBundle bundleDubInfo;
	protected final BundlePath bundlePath;
	protected final List<BundleResolution> depResolutions;
	
	public BundleResolution(SemanticManager manager, ResolvedManifest manifest) {
		super(manager, manifest.getBundle().getEffectiveImportFolders_AbsolutePath());
		this.manifest = manifest;
		this.bundleDubInfo = manifest.getBundle();
		this.bundlePath = assertNotNull(manifest.getBundlePath());
		this.depResolutions = Collections.unmodifiableList(createDepSRs(manager, manifest));
	}
	
	protected static List<BundleResolution> createDepSRs(SemanticManager manager, ResolvedManifest manifest) {
		List<BundleResolution> depSRs = new ArrayList<>();
		for (ResolvedManifest depManifest : manifest.getBundleDeps()) {
			depSRs.add(new BundleResolution(manager, depManifest));
		}
		return depSRs;
	}
	
	public String getBundleName() {
		return bundleDubInfo.getBundleName();
	}
	
	public BundlePath getBundlePath() {
		return bundlePath;
	}
	
	public DubBundle getBundle() {
		return bundleDubInfo;
	}
	
	public List<BundleResolution> getDirectDependencies() {
		return depResolutions;
	}
	
	@Override
	public String toString() {
		return "BundleResolution: " + getBundleName() + " - " + getBundlePath();
	}
	
	public boolean checkIsModuleListStale() {
		ArrayList<Path> importFolders = bundleDubInfo.getEffectiveImportFolders_AbsolutePath();
		BundleModulesVisitor modulesVisitor = new SM_BundleModulesVisitor(importFolders) {
			@Override
			protected void addModuleEntry(ModuleFullName moduleFullName, Path fullPath) {
				moduleFiles.add(fullPath);
			}
		};
		return !modulesVisitor.getModuleFiles().equals(bundleModules.moduleFiles);
	}
	
	public boolean checkIsModuleListStaleInTree() {
		if(checkIsModuleListStale()) {
			return true;
		}
		for (BundleResolution bundleRes : depResolutions) {
			if(bundleRes.checkIsModuleListStale()) {
				return true;
			}
		}
		return false;
	}
	
	/* -----------------  ----------------- */
	
	// TODO: proper synchronization
	protected final Map<Path, ResolvedModule> bundleResolutionModules = new HashMap<>();
	
	public synchronized ResolvedModule getResolvedModule(Path filePath) throws ParseSourceException {
		ModuleParseCache parseCache = manager.parseCache;
		
		
		ResolvedModule resolutionModule = bundleResolutionModules.get(filePath);
		if(resolutionModule == null) {
			ParsedModule parsedModule = parseCache.getParsedModule(filePath);
			resolutionModule = new ResolvedModule(parsedModule, this);
			bundleResolutionModules.put(filePath, resolutionModule);
		}
		return resolutionModule;
	}
	
	public synchronized boolean checkIsModuleContentsStale() {
		ModuleParseCache parseCache = manager.parseCache;
		
		for (Entry<Path, ResolvedModule> entry : bundleResolutionModules.entrySet()) {
			Path path = entry.getKey();
			ResolvedModule currentModule = entry.getValue();
			
			ParsedModule parsedModuleIfNotStale = parseCache.getEntry(path).getParsedModuleIfNotStale();
			if(parsedModuleIfNotStale == null) {
				return true;
				/*BUG here : minor optimization: if stale source ends up being the same, 
				 * then parsedModule needs not to be change. */
			}
			
			if(parsedModuleIfNotStale != currentModule.parsedModule) {
				return true;
			}
		}
		return false;
	}
	
	public boolean checkIsModuleContentsStaleInTree() {
		if(checkIsModuleContentsStale()) {
			return true;
		}
		for (BundleResolution bundleRes : depResolutions) {
			if(bundleRes.checkIsModuleContentsStale()) {
				return true;
			}
		}
		return false;
	}
	
	protected ResolvedModule getBundleResolvedModule(String moduleFullName) throws ParseSourceException {
		return getBundleResolvedModule(new ModuleFullName(moduleFullName));
	}
	
	protected ResolvedModule getBundleResolvedModule(ModuleFullName moduleFullName) throws ParseSourceException {
		Path modulePath = getBundleModuleAbsolutePath(moduleFullName);
		return modulePath == null ? null : getResolvedModule(modulePath);
	}
	
	public ParsedModule getBundleParsedModule(ModuleFullName moduleFullName) throws ParseSourceException {
		Path modulePath = getBundleModuleAbsolutePath(moduleFullName);
		return modulePath == null ? null : getResolvedModule(modulePath).parsedModule;
	}
	
	// TODO : /*BUG here test find resolved module of deps */
	public ResolvedModule findResolvedModule(ModuleFullName moduleFullName) throws ParseSourceException {
		ResolvedModule resolvedModule = getBundleResolvedModule(moduleFullName);
		if(resolvedModule != null) 
			return resolvedModule;
		
		for (BundleResolution depBundleRes : depResolutions) {
			resolvedModule = depBundleRes.findResolvedModule(moduleFullName);
			
			if(resolvedModule != null) 
				return resolvedModule;
		}
		return null;
	}
	
	
	public static class CommonResolvedModule {
		
		protected final ParsedModule parsedModule;
		protected final IModuleResolver mr;
		
		public CommonResolvedModule(ParsedModule parsedModule, IModuleResolver mr) {
			this.parsedModule = parsedModule;
			this.mr = mr;
		}
		
		public Module getModuleNode() {
			return parsedModule.module;
		}
		
		public ParsedModule getParsedModule() {
			return parsedModule;
		}
		
		public IModuleResolver getModuleResolver() {
			return mr;
		}
		
		public Path getModulePath() {
			return parsedModule.modulePath;
		}
		
	}
	
	public static class ResolvedModule extends CommonResolvedModule {
		
		protected final BundleResolution bundleRes;
		
		public ResolvedModule(ParsedModule parsedModule, BundleResolution bundleRes) {
			super(parsedModule, bundleRes);
			this.bundleRes = bundleRes;
		}
		
		public BundleResolution getSemanticResolution() {
			return bundleRes;
		}
		
	}
	
	
	/* ----------------- ----------------- */
	
	@Override
	public HashSet<String> findModules(String fullNamePrefix) {
		HashSet<String> matchedModules = new HashSet<String>();
		findModules(fullNamePrefix, matchedModules);
		return matchedModules;
	}
	
	protected void findModules(String fullNamePrefix, HashSet<String> matchedModules) {
		internalFindModules(fullNamePrefix, matchedModules);
		for (BundleResolution depSR : depResolutions) {
			depSR.findModules(fullNamePrefix, matchedModules);
		}
	}
	
	protected void internalFindModules(String fullNamePrefix, HashSet<String> matchedModules) {
		bundleModules.findModules(fullNamePrefix, matchedModules);
	}
	
	@Override
	public Module findModule(String[] packages, String module) throws ParseSourceException {
		ModuleFullName moduleFullName = new ModuleFullName(ArrayUtil.concat(packages, module));
		ResolvedModule resolvedModule = findResolvedModule(moduleFullName);
		return resolvedModule == null ? null : resolvedModule.getModuleNode();
	}
	
}