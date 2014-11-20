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


import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import melnorme.lang.tooling.ast.IModuleNode;
import melnorme.lang.tooling.bundles.ISemanticContext;
import melnorme.lang.tooling.bundles.ModuleFullName;
import melnorme.lang.tooling.bundles.ModuleSourceException;
import melnorme.lang.tooling.engine.IElementSemantics;
import melnorme.lang.tooling.engine.ResolutionEntry;
import melnorme.lang.tooling.util.EntryMap;
import dtool.ast.definitions.Module;
import dtool.engine.modules.BundleModulesVisitor;
import dtool.parser.DeeParserResult.ParsedModule;

public abstract class AbstractBundleResolution implements ISemanticContext {
	
	protected final SemanticManager manager;
	protected final BundleModules bundleModules;
	
	public AbstractBundleResolution(SemanticManager manager, List<Path> importFolders) {
		this(manager, manager.createBundleModules(importFolders));
	}
	
	public AbstractBundleResolution(SemanticManager manager, BundleModules bundleModules) {
		this.manager = manager;
		this.bundleModules = bundleModules;
	}
	
	
	public Set<Path> getBundleModuleFiles() {
		return bundleModules.moduleFiles;
	}
	
	public boolean bundleContainsModule(Path path) {
		return bundleModules.moduleFiles.contains(path);
	}
	
	/** @return the absolute path of a module contained in this bundle resolution, or null if not found. */
	protected Path getBundleModulePath(ModuleFullName moduleFullName) {
		return bundleModules.getModuleAbsolutePath(moduleFullName);
	}
	
	/* ----------------- ----------------- */
	
	@Override
	public HashSet<String> findModules(String fullNamePrefix) {
		HashSet<String> matchedModules = new HashSet<String>();
		findModules(fullNamePrefix, matchedModules);
		return matchedModules;
	}
	
	protected abstract void findModules(String fullNamePrefix, HashSet<String> matchedModules);

	protected void findBundleModules(String fullNamePrefix, HashSet<String> matchedModules) {
		bundleModules.findModules(fullNamePrefix, matchedModules);
	}
	
	/* -----------------  ----------------- */
	
	public boolean checkIsStale() {
		return checkIsModuleListStale() || checkIsModuleContentsStale();
	}
	
	public boolean checkIsModuleListStale() {
		BundleModulesVisitor modulesVisitor = manager.new SM_BundleModulesVisitor(bundleModules.importFolders);
		Set<Path> currentModules = modulesVisitor.getModuleFiles();
		return !currentModules.equals(bundleModules.moduleFiles);
	}
	
	/* -----------------  ----------------- */
	
	// TODO: proper synchronization - for now assume no concurrent acesss to resolve operations 
	protected final Map<Path, ResolvedModule> resolvedModules = new HashMap<>();
	
	public synchronized boolean checkIsModuleContentsStale() {
		ModuleParseCache parseCache = manager.parseCache;
		
		for (Entry<Path, ResolvedModule> entry : resolvedModules.entrySet()) {
			Path path = entry.getKey();
			ResolvedModule currentModule = entry.getValue();
			
			ParsedModule parsedModule = parseCache.getEntry(path).getParsedModuleIfNotStale(true);
			if(parsedModule == null) {
				return true;
			}
			
			if(parsedModule != currentModule.parsedModule) {
				return true;
			}
		}
		return false;
	}
	
	protected ResolvedModule getBundleResolvedModule(String moduleFullName) throws ModuleSourceException {
		return getBundleResolvedModule(new ModuleFullName(moduleFullName));
	}
	
	/** @return the module contained in this bundle, denoted by given moduleFullName, or null if none exists. */
	protected ResolvedModule getBundleResolvedModule(ModuleFullName moduleFullName) throws ModuleSourceException {
		Path modulePath = getBundleModulePath(moduleFullName);
		if(modulePath == null)
			return null;
		
		return getOrCreateBundleResolvedModule(modulePath);
	}
	
	/** @return the module contained in this bundle, denoted by given modulePath, or null if none exists. */
	protected ResolvedModule getBundleResolvedModule(Path modulePath) throws ModuleSourceException {
		if(!bundleContainsModule(modulePath))
			return null;
		
		return getOrCreateBundleResolvedModule(modulePath);
	}
	
	protected synchronized ResolvedModule getOrCreateBundleResolvedModule(Path filePath) throws ModuleSourceException {
		assertTrue(bundleContainsModule(filePath));
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
	public Module findModule(ModuleFullName moduleFullName) throws ModuleSourceException {
		ResolvedModule resolvedModule = findResolvedModule(moduleFullName);
		return resolvedModule == null ? null : resolvedModule.getModuleNode();
	}
	
	public IModuleNode findModuleNode(ModuleFullName moduleFullName) throws ModuleSourceException {
		ResolvedModule resolvedModule = findResolvedModule(moduleFullName);
		return resolvedModule == null ? null : resolvedModule.getModuleNode();
	}
	
	/** @return a resolved module from this bundle's full import path (including dependencies). */
	public abstract ResolvedModule findResolvedModule(ModuleFullName moduleFullName) throws ModuleSourceException;
	
	public abstract ResolvedModule findResolvedModule(Path path) throws ModuleSourceException;

	
	/* ----------------- NodeSemantics ----------------- */
	
	protected final ResolutionsMap resolutionsMap = new ResolutionsMap();
	
	public static class ResolutionsMap extends EntryMap<IElementSemantics, ResolutionEntry<?>> {
		
		@Override
		protected ResolutionEntry<?> createEntry(IElementSemantics key) {
			return new ResolutionEntry<>();
		}
		
	}
	
	@Override
	public ResolutionEntry<?> findResolutionEntryForContainedElement(IElementSemantics elementSemantics) {
		/* FIXME: ensure elementSemantics belongs to this context */
		return resolutionsMap.getEntry(elementSemantics);
	}
	
}