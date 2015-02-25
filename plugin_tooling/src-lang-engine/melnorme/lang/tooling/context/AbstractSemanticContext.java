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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import melnorme.lang.tooling.ast.CommonLanguageElement;
import melnorme.lang.tooling.engine.IElementSemanticData;
import melnorme.lang.tooling.engine.PickedElement;
import melnorme.lang.utils.EntryMap;
import melnorme.utilbox.misc.Location;

public abstract class AbstractSemanticContext implements ISemanticContext {
	
	protected final BundleModules bundleModules;
	
	public AbstractSemanticContext(BundleModules bundleModules) {
		this.bundleModules = assertNotNull(bundleModules);
	}
	
	public Map<ModuleFullName, Location> getBundleModulesMap() {
		return bundleModules.modules;
	}
	
	public Set<Location> getBundleModuleFiles() {
		return bundleModules.moduleFiles;
	}
	
	public boolean bundleContainsModule(Location modulePath) {
		return bundleModules.moduleFiles.contains(modulePath);
	}
	
	@SuppressWarnings("unused")
	public boolean bundleContainsElement(CommonLanguageElement languageElement, Location modulePath) {
		return bundleContainsModule(modulePath);
	}
	
	/** @return the absolute path of a module contained in this bundle resolution, or null if not found. */
	protected Location getBundleModulePath(ModuleFullName moduleFullName) {
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
	
	protected final SemanticsMap semanticsMap = new SemanticsMap();
	
	public class SemanticsMap extends EntryMap<CommonLanguageElement, IElementSemanticData> {
		
		@Override
		protected IElementSemanticData createEntry(CommonLanguageElement key) {
			return key.createSemantics(new PickedElement<>(key, AbstractSemanticContext.this));
		}
		
		protected HashMap<CommonLanguageElement, IElementSemanticData> getMap() {
			return map;
		}
		
	}
	
	@Override
	public final IElementSemanticData getSemanticsEntry(CommonLanguageElement element) {
		assertTrue(element.getElementSemanticContext(this) == this);
		return semanticsMap.getEntry(element);
	}
	
	@Override
	public void _resetSemantics() {
		semanticsMap.getMap().clear();
	}
	
}