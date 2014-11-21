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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dtool.engine.modules.ModuleNamingRules;
import melnorme.lang.tooling.bundles.ModuleFullName;

public class BundleModules {
	
	protected final Map<ModuleFullName, Path> modules;
	protected final Set<Path> moduleFiles;
	protected final List<Path> importFolders;
	
	/**
	 * Optimized constructor 
	 */
	public BundleModules(HashMap<ModuleFullName, Path> modules, HashSet<Path> moduleFiles, List<Path> importFolders) {
		this(modules, moduleFiles, importFolders, true);
	}
	
	public BundleModules(HashMap<ModuleFullName, Path> modules, HashSet<Path> moduleFiles, List<Path> importFolders, 
			boolean requireAbsolute) {
		this.modules = Collections.unmodifiableMap(modules);
		this.moduleFiles = Collections.unmodifiableSet(moduleFiles);
		this.importFolders = Collections.unmodifiableList(new ArrayList<>(importFolders));
		
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
	
	public Map<ModuleFullName, Path> getModules() {
		return modules;
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
	
	public static BundleModules createSyntheticBundleModules(Path filePath) {
		HashMap<ModuleFullName, Path> modules = new HashMap<>();
		HashSet<Path> moduleFiles = new HashSet<>();
		
		moduleFiles.add(filePath);
		ModuleFullName moduleFullName = ModuleNamingRules.getValidModuleNameOrNull(filePath.getFileName());
		modules.put(moduleFullName, filePath);
		
		return new BundleModules(modules, moduleFiles, new ArrayList<Path>(), false);
	}
	
}