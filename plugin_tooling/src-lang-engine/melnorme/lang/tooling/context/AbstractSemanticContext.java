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
package melnorme.lang.tooling.context;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import melnorme.lang.tooling.ast.CommonSemanticElement;
import melnorme.lang.tooling.ast.ISemanticElement;
import melnorme.lang.tooling.engine.IElementSemantics;
import melnorme.lang.tooling.engine.ResolutionEntry;
import melnorme.lang.utils.EntryMap;

public abstract class AbstractSemanticContext implements ISemanticContext {
	
	protected final BundleModules bundleModules;
	
	public AbstractSemanticContext(BundleModules bundleModules) {
		this.bundleModules = assertNotNull(bundleModules);
	}
	
	public Map<ModuleFullName, Path> getBundleModulesMap() {
		return bundleModules.modules;
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
	
	/* ----------------- NodeSemantics ----------------- */
	
	/* FIXME: BUG here remove this */
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
	public ISemanticContext findSemanticContext(ISemanticElement element) {
		return this; // subclasses must reimplement, if appropriate
	}
	
	protected final SemanticsMap semanticsMap = new SemanticsMap();
	
	public class SemanticsMap extends EntryMap<CommonSemanticElement, IElementSemantics> {
		
		@Override
		protected IElementSemantics createEntry(CommonSemanticElement key) {
			return key.createSemantics(AbstractSemanticContext.this);
		}
		
	}
	
	@Override
	public IElementSemantics getSemanticsEntry(CommonSemanticElement element) {
		assertTrue(findSemanticContext(element) == this);
		
		return semanticsMap.getEntry(element);
	}
	
}