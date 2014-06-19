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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import melnorme.utilbox.misc.ArrayUtil;
import dtool.ast.definitions.Module;
import dtool.dub.BundlePath;
import dtool.dub.DubBundle;
import dtool.dub.ResolvedManifest;
import dtool.engine.ModuleParseCache.ParseSourceException;
import dtool.engine.modules.IModuleResolver;
import dtool.engine.modules.ModuleFullName;
import dtool.parser.DeeParserResult.ParsedModule;

public class BundleResolution implements IModuleResolver {
	
	protected final SemanticManager manager;
	protected final ResolvedManifest manifest;
	protected final DubBundle bundleDubInfo;
	protected final BundlePath bundlePath;
	protected final List<BundleResolution> depResolutions;
	
	protected final Map<ModuleFullName, Path> modules;
	protected final Set<Path> moduleFiles;
	
	public BundleResolution(SemanticManager manager, ResolvedManifest manifest) {
		this.manager = manager;
		this.manifest = manifest;
		this.bundleDubInfo = manifest.getBundle();
		this.bundlePath = assertNotNull(manifest.getBundlePath());
		this.depResolutions = Collections.unmodifiableList(createDepSRs(manager, manifest));
		
		
		BundleModulesVisitor modulesHelper = new BundleModulesVisitor(manager.dtoolServer, bundleDubInfo) {
			@Override
			protected void addModuleEntry(ModuleFullName moduleFullName, Path fullPath) {
				modules.put(moduleFullName, fullPath);
				moduleFiles.add(fullPath);
			}
		};
		
		this.modules = Collections.unmodifiableMap(modulesHelper.modules);
		this.moduleFiles = Collections.unmodifiableSet(modulesHelper.moduleFiles);
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
	
	public Map<ModuleFullName, Path> getBundleModules() {
		return modules;
	}
	
	public Set<Path> getBundleModuleFiles() {
		return moduleFiles;
	}
	
	@Override
	public String toString() {
		return "BundleResolution: " + getBundleName() + " - " + getBundlePath();
	}
	
	public Path getBundleModuleAbsolutePath(ModuleFullName moduleFullName) {
		Path path = modules.get(moduleFullName);
		if(path == null)
			return null;
		return getBundlePath().resolve(path);
	}
	
	public boolean checkIsModuleListStale() {
		BundleModulesVisitor modulesVisitor = new BundleModulesVisitor(manager.dtoolServer, bundleDubInfo) {
			@Override
			protected void addModuleEntry(ModuleFullName moduleFullName, Path fullPath) {
				moduleFiles.add(fullPath);
			}
		};
		return !modulesVisitor.moduleFiles.equals(moduleFiles);
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
		return getBundleResolvedModule(new ModuleFullName(moduleFullName) /*BUG here*/);
	}
	
	protected ResolvedModule getBundleResolvedModule(ModuleFullName moduleFullName) throws ParseSourceException {
		Path modulePath = getBundleModuleAbsolutePath(moduleFullName);
		return modulePath == null ? null : getResolvedModule(modulePath);
	}
	
	public ParsedModule getBundleParsedModule(ModuleFullName moduleFullName) throws ParseSourceException {
		Path modulePath = getBundleModuleAbsolutePath(moduleFullName);
		return modulePath == null ? null : getResolvedModule(modulePath).parsedModule;
	}
	
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
	
	public HashSet<String> findModules2(String fullNamePrefix) {
		String[] modules = findModules(fullNamePrefix);
		HashSet<String> hashSet = new HashSet<String>();
		hashSet.addAll(Arrays.asList(modules));
		return hashSet;
	}
	
	@Deprecated
	@Override
	public String[] findModules(String fullNamePrefix) {
		ArrayList<String> matchedModules = new ArrayList<>();
		
		findModules(fullNamePrefix, matchedModules);
		
		return matchedModules.toArray(new String[0]);
	}
	
	protected void findModules(String fullNamePrefix, ArrayList<String> matchedModules) {
		internalFindModules(fullNamePrefix, matchedModules);
		for (BundleResolution depSR : depResolutions) {
			depSR.findModules(fullNamePrefix, matchedModules);
		}
	}
	
	protected void internalFindModules(String fullNamePrefix, ArrayList<String> matchedModules) {
		Set<ModuleFullName> moduleEntries = modules.keySet();
		for (ModuleFullName moduleEntry : moduleEntries) {
			String moduleFullName = moduleEntry.getNameAsString();
			if(moduleFullName.startsWith(fullNamePrefix)) {
				matchedModules.add(moduleFullName);
			}
		}
	}
	
	@Override
	public Module findModule(String[] packages, String module) throws ParseSourceException {
		ModuleFullName moduleFullName = new ModuleFullName(ArrayUtil.concat(packages, module));
		ResolvedModule resolvedModule = findResolvedModule(moduleFullName);
		return resolvedModule == null ? null : resolvedModule.getModuleNode();
	}
	
}