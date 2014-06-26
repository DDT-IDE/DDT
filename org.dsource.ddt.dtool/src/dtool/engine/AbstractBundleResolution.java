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


import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import melnorme.utilbox.misc.ArrayUtil;
import dtool.ast.definitions.Module;
import dtool.engine.ModuleParseCache.ParseSourceException;
import dtool.engine.modules.BundleModulesVisitor;
import dtool.engine.modules.IModuleResolver;
import dtool.engine.modules.ModuleFullName;
import dtool.parser.DeeParserResult.ParsedModule;

public abstract class AbstractBundleResolution implements IBundleResolution {
	
	protected final SemanticManager manager;
	protected final BundleModules bundleModules;
	
	public AbstractBundleResolution(SemanticManager manager, List<Path> importFolders) {
		this(manager, manager.new SM_BundleModulesVisitor(importFolders).toBundleModules());
	}
	
	public AbstractBundleResolution(SemanticManager manager, BundleModules bundleModules) {
		this.manager = manager;
		this.bundleModules = bundleModules;
	}
	
	
	public Set<Path> getBundleModuleFiles() {
		return bundleModules.moduleFiles;
	}
	
	/** @return the absolute path of a module contained in this bundle resolution. */
	protected Path getBundleModulePath(ModuleFullName moduleFullName) {
		return bundleModules.getModuleAbsolutePath(moduleFullName);
	}
	
	@Override
	public HashSet<String> findModules(String fullNamePrefix) {
		HashSet<String> matchedModules = new HashSet<String>();
		findModules(fullNamePrefix, matchedModules);
		return matchedModules;
	}
	
	protected void findModules(String fullNamePrefix, HashSet<String> matchedModules) {
		bundleModules.findModules(fullNamePrefix, matchedModules);
	}
	
	public boolean checkIsStale() {
		return checkIsModuleListStale() || checkIsModuleContentsStale();
	}
	
	public boolean checkIsModuleListStale() {
		List<Path> importFolders = bundleModules.importFolders;
		BundleModulesVisitor modulesVisitor = manager.new SM_BundleModulesVisitor(importFolders) {
			@Override
			protected void addModuleEntry(ModuleFullName moduleFullName, Path fullPath) {
				moduleFiles.add(fullPath);
			}
		};
		return !modulesVisitor.getModuleFiles().equals(bundleModules.moduleFiles);
	}
	
	/* -----------------  ----------------- */
	
	// TODO: proper synchronization - for now assume no concurrent acesss to resolve operations 
	protected final Map<Path, ResolvedModule> resolvedModules = new HashMap<>();
	
	public synchronized boolean checkIsModuleContentsStale() {
		ModuleParseCache parseCache = manager.parseCache;
		
		for (Entry<Path, ResolvedModule> entry : resolvedModules.entrySet()) {
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
	
	protected ResolvedModule getBundleResolvedModule(String moduleFullName) throws ParseSourceException {
		return getBundleResolvedModule(new ModuleFullName(moduleFullName));
	}
	
	protected ResolvedModule getBundleResolvedModule(ModuleFullName moduleFullName) throws ParseSourceException {
		Path modulePath = getBundleModulePath(moduleFullName);
		return modulePath == null ? null : getBundleResolvedModule(modulePath);
	}
	
	public synchronized ResolvedModule getBundleResolvedModule(Path filePath) throws ParseSourceException {
		ModuleParseCache parseCache = manager.parseCache;
		
		ResolvedModule resolvedModule = resolvedModules.get(filePath);
		if(resolvedModule == null) {
			ParsedModule parsedModule = parseCache.getParsedModule(filePath);
			resolvedModule = new ResolvedModule(parsedModule, this);
			resolvedModules.put(filePath, resolvedModule);
		}
		return resolvedModule;
	}
	
	@Override
	public Module findModule(String[] packages, String module) throws ParseSourceException {
		ModuleFullName moduleFullName = new ModuleFullName(ArrayUtil.concat(packages, module));
		ResolvedModule resolvedModule = findResolvedModule(moduleFullName);
		return resolvedModule == null ? null : resolvedModule.getModuleNode();
	}
	
	/** @return a resolved module from this bundle's full import path (including dependencies). */
	public abstract ResolvedModule findResolvedModule(ModuleFullName moduleFullName) throws ParseSourceException;
	
	
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
		
		protected final AbstractBundleResolution bundleRes;
		
		public ResolvedModule(ParsedModule parsedModule, AbstractBundleResolution bundleRes) {
			super(parsedModule, bundleRes);
			this.bundleRes = bundleRes;
		}
		
		public AbstractBundleResolution getSemanticResolution() {
			return bundleRes;
		}
		
	}
	
}