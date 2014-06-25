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


import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import dtool.engine.modules.BundleModulesVisitor;
import dtool.engine.modules.ModuleFullName;

public class AbstractBundleResolution {
	
	protected final SemanticManager manager;
	protected final BundleModules bundleModules;
	
	public AbstractBundleResolution(SemanticManager manager, List<Path> importFolders) {
		this.manager = manager;
		this.bundleModules = new SM_BundleModulesVisitor(importFolders).toBundleModules();
	}
	
	protected class SM_BundleModulesVisitor extends BundleModulesVisitor {
		public SM_BundleModulesVisitor(List<Path> importFolders) {
			super(importFolders);
		}
		
		@Override
		protected FileVisitResult handleFileVisitException(Path file, IOException exc) {
			manager.dtoolServer.logError("Error visiting path/director: " + file, exc);
			return FileVisitResult.CONTINUE;
		}
	}
	
	public Set<Path> getBundleModuleFiles() {
		return bundleModules.moduleFiles;
	}
	
	public Path getBundleModuleAbsolutePath(ModuleFullName moduleFullName) {
		return bundleModules.getModuleAbsolutePath(moduleFullName);
	}
	
}