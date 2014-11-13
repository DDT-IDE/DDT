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

import melnorme.lang.tooling.ast.IModuleNode;
import melnorme.lang.tooling.ast_actual.ILangNamedElement;
import melnorme.lang.tooling.bundles.ISemanticResolution;
import melnorme.lang.tooling.bundles.ModuleFullName;
import melnorme.lang.tooling.engine.scoping.ScopeSemantics;
import melnorme.lang.tooling.symbols.ElementName;
import melnorme.utilbox.misc.StringUtil;
import dtool.ast.definitions.Module;
import dtool.engine.ModuleParseCache.ParseSourceException;
import dtool.engine.modules.BundleModulesVisitor;
import dtool.parser.DeeParserResult.ParsedModule;

public abstract class AbstractBundleResolution implements ISemanticResolution {
	
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
	
	/** @return a resolved module from this bundle's full import path (including dependencies). */
	public ResolvedModule findResolvedModule(ModuleFullName moduleFullName) throws ParseSourceException {
		return getBundleResolvedModule(moduleFullName);
	}
	
	@Override
	public ResolvedModule findCompilationUnit(ModuleFullName moduleName) throws ParseSourceException {
		return findResolvedModule(moduleName);
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
	
	protected ResolvedModule getBundleResolvedModule(String moduleFullName) throws ParseSourceException {
		return getBundleResolvedModule(new ModuleFullName(moduleFullName));
	}
	
	/** @return the module contained in this bundle, denoted by moduleFullName, or null if not found. */
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
	public Module findModule(ModuleFullName moduleFullName) throws ParseSourceException {
		ResolvedModule resolvedModule = findResolvedModule(moduleFullName);
		return resolvedModule == null ? null : resolvedModule.getModuleNode();
	}
	
	public IModuleNode findModuleNode(ModuleFullName moduleFullName) throws ParseSourceException {
		ResolvedModule resolvedModule = findResolvedModule(moduleFullName);
		return resolvedModule == null ? null : resolvedModule.getModuleNode();
	}
	
	
	/* ----------------- used by tests only, at the moment ----------------- */
	
	public ILangNamedElement findContainedElement(String elementName) throws ParseSourceException {
		ElementName name = new ElementName(elementName);
		
		String possibleModuleName = null;
		for (String segment : name.getSegments()) {
			
			possibleModuleName = possibleModuleName == null ? 
					segment :
					possibleModuleName + ElementName.NAME_SEP + segment;
			
			ResolvedModule mr = getBundleResolvedModule(possibleModuleName);
			if(mr != null) {
				String elementSubName = StringUtil.segmentAfterMatch(elementName, 
					possibleModuleName + ElementName.NAME_SEP);
				return ScopeSemantics.findElement(mr.getModuleNode(), elementSubName);
			}
		}
		return null;
	}
	
}