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
package melnorme.lang.tooling.bundles;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.HashSet;
import java.util.Set;

import melnorme.lang.tooling.engine.IElementSemantics;
import melnorme.lang.tooling.engine.ResolutionEntry;
import melnorme.utilbox.misc.ArrayUtil;
import dtool.ast.definitions.Module;

/**
 * A mock semantic resolution. This implementation finds no modules.
 */
public class MockSemanticResolution implements ISemanticContext {
	
	@Override
	public Set<String> findModules(String fqNamePrefix) {
		assertNotNull(fqNamePrefix);
		return findModules_do(fqNamePrefix);
	}
	
	@SuppressWarnings("unused")
	protected Set<String> findModules_do(String fqNamePrefix) {
		return new HashSet<>();
	}
	
	public Module findModule(String[] packages, String module) throws ModuleSourceException {
		assertNotNull(packages);
		assertTrue(ArrayUtil.contains(packages, null) == false);
		assertTrue(ArrayUtil.contains(packages, "") == false);
		assertNotNull(module);
		return findModule_do(packages, module);
	}
	
	@Override
	public Module findModule(ModuleFullName moduleName) throws ModuleSourceException {
		return findModule(moduleName.getPackages(), moduleName.getLastSegment());
	}
	
	@SuppressWarnings("unused")
	protected Module findModule_do(String[] packages, String module) throws ModuleSourceException {
		return null;
	}
	
	@Override
	public ResolutionEntry<?> findResolutionEntryForContainedElement(IElementSemantics elementSemantics) {
		return null;
	}
	
}