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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import dtool.engine.modules.ModuleFullName;

public class BundleModules {
	
	protected final Map<ModuleFullName, Path> modules;
	protected final Set<Path> moduleFiles;
	
	/**
	 * Optimized constructor 
	 */
	public BundleModules(HashMap<ModuleFullName, Path> modules, HashSet<Path> moduleFiles) {
		this(modules, moduleFiles, true);
	}
	
	public BundleModules(HashMap<ModuleFullName, Path> modules, HashSet<Path> moduleFiles, boolean requireAbsolute) {
		this.modules = Collections.unmodifiableMap(modules);
		this.moduleFiles = Collections.unmodifiableSet(moduleFiles);
		
		if(requireAbsolute) {
			for (Path path : moduleFiles) {
				assertTrue(path.isAbsolute());
			}
			for (Path path : modules.values()) {
				assertTrue(path.isAbsolute());
			}
		}
	}
	
	public Set<Path> getModuleFiles() {
		return moduleFiles;
	}
	
	public Path getModuleAbsolutePath(ModuleFullName moduleFullName) {
		return modules.get(moduleFullName);
	}
	
	protected void findModules(String fullNamePrefix, HashSet<String> matchedModules) {
		Set<ModuleFullName> moduleEntries = modules.keySet();
		for (ModuleFullName moduleEntry : moduleEntries) {
			String moduleFullName = moduleEntry.getFullNameAsString();
			if(moduleFullName.startsWith(fullNamePrefix)) {
				matchedModules.add(moduleFullName);
			}
		}
	}
	
}