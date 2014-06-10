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
package dtool.model;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import melnorme.utilbox.misc.ArrayUtil;
import dtool.ast.definitions.Module;
import dtool.dub.BundlePath;
import dtool.dub.DubBundle;
import dtool.dub.ResolvedManifest;
import dtool.model.ModuleParseCache.ParseSourceException;
import dtool.parser.DeeParserResult.ParsedModule;
import dtool.project.IModuleResolver;

public class BundleSemanticResolution extends ResolvedManifest implements IModuleResolver {
	
	protected final SemanticManager manager;
	
	public BundleSemanticResolution(SemanticManager manager, DubBundle bundle, ArrayList<BundlePath> depBundlePaths, 
			Map<ModuleFullName, Path> bundleModules) {
		super(bundle, depBundlePaths, bundleModules);
		this.manager = manager;
	}
	
	public String getBundleId() {
		return bundle.getBundleName();
	}
	
	public Path internalGetModuleAbsolutePath(ModuleFullName moduleFullName) {
		Path path = bundleModules.get(moduleFullName);
		if(path == null)
			return null;
		return getBundlePath().resolve(path);
	}
	
	/* ----------------- ----------------- */
	
	protected BundleSemanticResolution getDepSR(BundlePath bundlePath) {
		try {
			return manager.getUpdatedResolution(bundlePath);
		} catch (ExecutionException e) {
			/*BUG here TODO*/
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
	}
	
	@Override
	public Module findModule(String[] packages, String module) throws Exception {
		ModuleFullName moduleFullName = new ModuleFullName(ArrayUtil.concat(packages, module));
		return getParsedModule(moduleFullName).getModuleNode();
	}
	
	public Path getModuleAbsolutePath(ModuleFullName moduleFullName) {
		Path modulePath = internalGetModuleAbsolutePath(moduleFullName);
		if(modulePath != null) {
			return modulePath;
		}
		for (BundlePath depBundlePath : getBundleDeps()) {
			modulePath = getDepSR(depBundlePath).internalGetModuleAbsolutePath(moduleFullName);
			if(modulePath != null) {
				return modulePath;
			}
		}
		return null;
	}
	
	@Override
	public String[] findModules(String fullNamePrefix) {
		ArrayList<String> matchedModules = new ArrayList<>();
		
		internalFindModules(fullNamePrefix, matchedModules);
		for (BundlePath depBundlePath : getBundleDeps()) {
			getDepSR(depBundlePath).internalFindModules(fullNamePrefix, matchedModules);;
		}
		
		return matchedModules.toArray(new String[0]);
	}
	
	protected void internalFindModules(String fullNamePrefix, ArrayList<String> matchedModules) {
		Set<ModuleFullName> moduleEntries = bundleModules.keySet();
		for (ModuleFullName moduleEntry : moduleEntries) {
			String moduleFullName = moduleEntry.getModuleFullName();
			if(moduleFullName.startsWith(fullNamePrefix)) {
				matchedModules.add(moduleFullName);
			}
		}
	}
	
	public ParsedModule getParsedModule(String moduleFullName) throws ParseSourceException {
		return getParsedModule(new ModuleFullName(moduleFullName));
	}
	
	public ParsedModule getParsedModule(ModuleFullName moduleFullName) throws ParseSourceException {
		Path filePath = getModuleAbsolutePath(moduleFullName);
		if(filePath == null) {
			return null;
		}
		return manager.parseCache.getParsedModule(filePath);
	}
	
}