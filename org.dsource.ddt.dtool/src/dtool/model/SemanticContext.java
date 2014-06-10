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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import melnorme.utilbox.misc.ArrayUtil;
import dtool.ast.definitions.Module;
import dtool.dub.BundlePath;
import dtool.dub.DubBundle;
import dtool.model.ModuleParseCache.ParseSourceException;
import dtool.model.SemanticManager.SemanticResolution;
import dtool.parser.DeeParserResult.ParsedModule;
import dtool.project.IModuleResolver;

public class SemanticContext implements IModuleResolver {
	
	protected final SemanticManager manager;
	protected final DubBundle bundle;
	protected final BundlePath bundlePath;
	protected final BundlePath[] bundleDeps;
	protected final Map<ModuleFullName, Path> bundleModules; //immutable
	
	public SemanticContext(SemanticManager manager, DubBundle bundle, BundlePath[] bundleDeps, 
			Map<ModuleFullName, Path> bundleModules) {
		this.manager = manager;
		this.bundle = assertNotNull(bundle);
		this.bundlePath = assertNotNull(bundle.getBundlePath());
		this.bundleDeps = ArrayUtil.removeAll(bundleDeps, null);
		
		this.bundleModules = bundleModules;
	}
	
	public String getBundleId() {
		return bundle.getBundleName();
	}
	
	public Map<ModuleFullName, Path> getBundleModuleFiles() {
		return bundleModules;
	}
	
	public Path internalGetModuleAbsolutePath(ModuleFullName moduleFullName) {
		Path path = bundleModules.get(moduleFullName);
		if(path == null)
			return null;
		return getBundlePath().resolve(path);
	}
	
	public BundlePath getBundlePath() {
		return bundlePath;
	}
	
	protected BundlePath[] getBundleDeps() {
		return bundleDeps;
	}
	
	/* ----------------- ----------------- */
	
	protected SemanticResolution getDepSR(BundlePath bundlePath) {
		return manager.getSemanticResolution(bundlePath);
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