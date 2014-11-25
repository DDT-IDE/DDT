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
import java.util.HashSet;
import java.util.Set;

import dtool.ast.definitions.Module;
import melnorme.lang.tooling.ast.IModuleNode;
import melnorme.lang.tooling.ast.ISemanticElement;
import melnorme.lang.tooling.bundles.ISemanticContext;
import melnorme.lang.tooling.bundles.ModuleFullName;
import melnorme.lang.tooling.bundles.ModuleSourceException;
import melnorme.lang.tooling.engine.IElementSemantics;
import melnorme.lang.tooling.engine.ResolutionEntry;
import melnorme.lang.utils.EntryMap;

public abstract class AbstractSemanticContext implements ISemanticContext {
	
	protected final BundleModules bundleModules;
	
	public AbstractSemanticContext(BundleModules bundleModules) {
		this.bundleModules = assertNotNull(bundleModules);
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
	
	@Override
	public HashSet<String> findModules(String fullNamePrefix) {
		HashSet<String> matchedModules = new HashSet<String>();
		findModules(fullNamePrefix, matchedModules);
		return matchedModules;
	}
	
	protected void findModules(String fullNamePrefix, HashSet<String> matchedModules) {
		findBundleModules(fullNamePrefix, matchedModules);
	}
	
	protected void findBundleModules(String fullNamePrefix, HashSet<String> matchedModules) {
		bundleModules.findModules(fullNamePrefix, matchedModules);
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
	
	/** @return a resolved module from for the module with the given name, from the modules
	 * available in this context (including dependencies). Can be null. */
	public abstract ResolvedModule findResolvedModule(ModuleFullName moduleFullName) throws ModuleSourceException;
	
	/** @return a resolved module from for the module with the given path, from the modules
	 * available in this context (including dependencies). Can be null. */
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
	
	@Override
	public ISemanticContext findSemanticContext(ISemanticElement Element) {
		return this; // TODO subclass must reimplement, if appropriate
	}
	
}