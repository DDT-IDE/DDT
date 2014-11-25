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

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import dtool.engine.AbstractSemanticContext;
import dtool.engine.BundleModules;
import dtool.engine.ResolvedModule;

/**
 * A mock semantic resolution. This implementation finds no modules.
 */
public class EmptySemanticResolution extends AbstractSemanticContext {
	
	public EmptySemanticResolution() {
		super(new BundleModules(new HashMap<ModuleFullName, Path>(), new HashSet<Path>(), new ArrayList<Path>()));
	}
	
	@Override
	public ResolvedModule findResolvedModule(ModuleFullName moduleFullName) throws ModuleSourceException {
		return null;
	}
	
	@Override
	public ResolvedModule findResolvedModule(Path path) throws ModuleSourceException {
		return null;
	}
	
}