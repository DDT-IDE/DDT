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
import melnorme.lang.tooling.ast.ISemanticElement;
import melnorme.lang.tooling.context.AbstractSemanticContext;
import melnorme.lang.tooling.context.BundleModules;
import melnorme.lang.tooling.context.ISemanticContext;
import melnorme.lang.tooling.context.ModuleFullName;
import melnorme.lang.tooling.context.ModuleSourceException;
import dtool.ast.definitions.Module;
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
	
	
	public <E extends Exception > void visitBundleResolutions(BundleResolutionVisitor<?, E> visitor) throws E {
		
	 	getStdLibResolution().visitBundleResolutions(visitor);
		if(visitor.isFinished()) {
			return;
		}
		
		visitBundleResolutionsAfterStdLib(visitor);
	}
	
	public <E extends Exception> void visitBundleResolutionsAfterStdLib(BundleResolutionVisitor<?, E> visitor) 
			throws E {
		visitor.visit(this);
	}
	
	public abstract class BundleResolutionVisitor<ELEM, EXC extends Exception> {
		
		public ELEM result;
		
		public ELEM findResult(AbstractBundleResolution bundleRes) throws EXC {
			bundleRes.visitBundleResolutions(this);
			return result;
		}
		
		protected boolean isFinished() {
			return result != null;
		}
		
		protected abstract void visit(AbstractBundleResolution bundleResolution) throws EXC;
		
	}
	
	@Override
	protected final void findModules(final String fullNamePrefix, final HashSet<String> matchedModules) {
		visitBundleResolutions(new BundleResolutionVisitor<Object, RuntimeException>() {
			@Override
			protected void visit(AbstractBundleResolution bundleResolution) {
				bundleResolution.findBundleModules(fullNamePrefix, matchedModules);
			}
			
			@Override
			protected boolean isFinished() {
				return false; // redudant, but here for clarity
			}
		});
	}
	
	/** @return a resolved module from for the module with the given name, from the modules
	 * available in this context (including dependencies). Can be null. */
	public ResolvedModule findResolvedModule(final ModuleFullName moduleFullName) throws ModuleSourceException {
		return new BundleResolutionVisitor<ResolvedModule, ModuleSourceException>() {
			@Override
			protected void visit(AbstractBundleResolution bundleResolution) throws ModuleSourceException {
				result = bundleResolution.getBundleResolvedModule(moduleFullName);
			}
		}.findResult(this);
	}
	
	/** @return a resolved module from for the module with the given path, from the modules
	 * available in this context (including dependencies). Can be null. */
	public ResolvedModule findResolvedModule(final Path path) throws ModuleSourceException {
		return new BundleResolutionVisitor<ResolvedModule, ModuleSourceException>() {
			@Override
			protected void visit(AbstractBundleResolution bundleResolution) throws ModuleSourceException {
				result = bundleResolution.getBundleResolvedModule(path);
			}
		}.findResult(this);
	}
	
	/** @return the bundle resolution that directly contains given modulePath, or null if none does. */
	public AbstractBundleResolution getContainingBundleResolution(final Path modulePath) {
		return new BundleResolutionVisitor<AbstractBundleResolution, RuntimeException>() {
			@Override
			protected void visit(AbstractBundleResolution bundleResolution) {
				if(bundleResolution.bundleContainsModule(modulePath)) {
					result = bundleResolution;
				}
			}
		}.findResult(this);
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
	
	@Override
	public Module findModule(ModuleFullName moduleFullName) throws ModuleSourceException {
		ResolvedModule resolvedModule = findResolvedModule(moduleFullName);
		return resolvedModule == null ? null : resolvedModule.getModuleNode();
	}
	
	public IModuleNode findModuleNode(ModuleFullName moduleFullName) throws ModuleSourceException {
		ResolvedModule resolvedModule = findResolvedModule(moduleFullName);
		return resolvedModule == null ? null : resolvedModule.getModuleNode();
	}
	
	
	/* -----------------  ----------------- */
	
	protected final Map<Path, ResolvedModule> resolvedModules = new HashMap<>();
	protected final Object resolvedModulesLock = new Object(); 
	
	public boolean checkIsModuleContentsStale() {
		ModuleParseCache parseCache = manager.parseCache;
		
		synchronized(resolvedModulesLock) {
			for(Entry<Path, ResolvedModule> entry : resolvedModules.entrySet()) {
				Path path = entry.getKey();
				ResolvedModule currentModule = entry.getValue();
				
				ParsedModule cacheModule = parseCache.getEntry(path).getParsedModuleIfNotStale();
				if(cacheModule == null) {
					return true; // Source has changed since last parse
				}
				
				if(cacheModule != currentModule.parsedModule) {
					return true; // Parse is up-to-date in the cache, but it's a newer module than the one here.
				}
			}
			return false;
		}
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
	
	protected ResolvedModule getOrCreateBundleResolvedModule(Path filePath) throws ModuleSourceException {
		assertTrue(bundleContainsModule(filePath));
		ModuleParseCache parseCache = manager.parseCache;
		
		synchronized(resolvedModulesLock) {
			ResolvedModule resolvedModule = resolvedModules.get(filePath);
			if(resolvedModule == null) {
				ParsedModule parsedModule = parseCache.getParsedModule(filePath);
				resolvedModule = new ResolvedModule(parsedModule, this);
				resolvedModules.put(filePath, resolvedModule);
			}
			return resolvedModule;
		}
	}
	
	/* ----------------- NodeSemantics ----------------- */
	
	@Override
	public ISemanticContext findSemanticContext(ISemanticElement element) {
		if(element.isLanguageIntrinsic()) {
			return getStdLibResolution();
		}
		
		if(element.getModulePath() == null) {
			return null;
		}
		
		return getContainingBundleResolution(element.getModulePath());
	}
	
}