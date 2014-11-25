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

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import melnorme.lang.tooling.symbols.INamedElement;

/**
 * A mock semantic resolution. This implementation finds no modules.
 */
public class EmptySemanticResolution extends AbstractSemanticContext {
	
	public EmptySemanticResolution() {
		super(new BundleModules(new HashMap<ModuleFullName, Path>(), new HashSet<Path>(), new ArrayList<Path>()));
	}
	
	@Override
	public INamedElement findModule(ModuleFullName moduleName) throws ModuleSourceException {
		return null;
	}
	
}