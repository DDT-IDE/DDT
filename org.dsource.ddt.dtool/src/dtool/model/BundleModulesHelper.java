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
import java.util.HashMap;

import melnorme.utilbox.misc.MiscUtil;
import dtool.dub.BundlePath;
import dtool.dub.DubBundle;
import dtool.dub.DubBundle.BundleFile;
import dtool.dub.DubBundle.DubDependecyRef;
import dtool.project.DeeNamingRules;

public class BundleModulesHelper {
	
	protected final DToolServer dtoolServer;
	
	public BundleModulesHelper(DToolServer dtoolServer) {
		this.dtoolServer = assertNotNull(dtoolServer);
	}
	
	protected void logError(String message) {
		dtoolServer.logError(message, null);
	}
	
	protected void logWarning(String message) {
		dtoolServer.logMessage(message);
	}
	
	/* ----------------- module model calculation ----------------- */
	
	protected BundlePath[] getDependenciesBundlePath(HashMap<String, BundlePath> bundleToPathMap, DubBundle bundle) {
		DubDependecyRef[] depRefs = bundle.getDependencyRefs();
		BundlePath[] directDepsPath = new BundlePath[depRefs.length];
		for (int i = 0; i < depRefs.length; i++) {
			directDepsPath[i] = bundleToPathMap.get(depRefs[i].getBundleName());
			if(directDepsPath[i] == null) {
				dtoolServer.logError("DUB describe: dependency path is missing or invalid.", null);
			}
		}
		return directDepsPath;
	}
	
	protected HashMap<ModuleFullName, Path> calculateBundleModules(DubBundle bundle) {
		HashMap<ModuleFullName, Path> hashMap = new HashMap<>();
		
		for (BundleFile bundleFiles : bundle.bundleFiles) {
			Path filePath = MiscUtil.createPathOrNull(bundleFiles.filePath);
			if(filePath == null) {
				logError("Invalid filesystem path: " + bundleFiles.filePath);
				continue; // ignore
			}
			
			Path[] importFolders = bundle.getEffectiveImportPathFolders();
			for (Path importFolder : importFolders) {
				if(filePath.startsWith(importFolder)) {
					Path relPath = importFolder.relativize(filePath);
					if(relPath.getNameCount() == 0) {
						logError("File has same path as import folder: " + filePath);
						continue;
					}
					
					ModuleFullName moduleFullName = DeeNamingRules.getModuleFullName(relPath);
					if(!moduleFullName.isValid()) {
						logWarning("Invalid path for a D module: " + relPath);
						continue;
					}
					hashMap.put(moduleFullName, filePath);
					
					// continue looking, the same file can be present in multiple import paths, if nested
					// it's not an elegant scenario, but it's probably ok to support.
				}
			}
		}
		return hashMap;
	}
	
}