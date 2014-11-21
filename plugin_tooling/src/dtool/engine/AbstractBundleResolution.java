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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import melnorme.lang.tooling.ast.ISemanticElement;
import melnorme.lang.tooling.bundles.ISemanticContext;
import melnorme.lang.tooling.bundles.ModuleFullName;
import melnorme.lang.tooling.bundles.ModuleSourceException;
import dtool.engine.modules.BundleModulesVisitor;
import dtool.parser.DeeParserResult.ParsedModule;

public abstract class AbstractBundleResolution extends AbstractSemanticContext {
	
	protected final SemanticManager manager;
	
	public AbstractBundleResolution(SemanticManager manager, List<Path> importFolders) {
		this(manager, manager.createBundleModules(importFolders));
	}
	
	public AbstractBundleResolution(SemanticManager manager, BundleModules bundleModules) {
		super(bundleModules);
		this.manager = manager;
	}
	
	
	public abstract StandardLibraryResolution getStdLibResolution();
	
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
	
	/* ----------------- NodeSemantics ----------------- */
	
	// /* FIXME: TODO test this method */
	@Override
	public ISemanticContext findSemanticContext(ISemanticElement element) {
		if(element.isLanguageIntrinsic()) {
			return getStdLibResolution();
		}
		
		try {
			Path modulePath = element.getModulePath();
			if(modulePath == null) {
				return null; // Cannot determine context
			}
			ResolvedModule resolvedModule = findResolvedModule(modulePath);
			if(resolvedModule == null) {
				return null; // Cannot determine context
			}
			return resolvedModule.getSemanticContext();
		} catch (ModuleSourceException e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
	}
	
}