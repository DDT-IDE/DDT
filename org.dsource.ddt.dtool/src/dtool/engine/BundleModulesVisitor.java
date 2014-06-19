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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import dtool.dub.DubBundle;
import dtool.engine.modules.ModuleFullName;
import dtool.engine.modules.ModuleNamingRules;

public class BundleModulesVisitor {
	
	protected final DToolServer dtoolServer;
	
	protected final HashMap<ModuleFullName, Path> modules = new HashMap<>();
	protected final HashSet<Path> moduleFiles = new HashSet<>();
	
	public BundleModulesVisitor(DToolServer dtoolServer, DubBundle bundle) {
		this.dtoolServer = assertNotNull(dtoolServer);
		visitBundleModules(bundle);
	}
	
	public void visitBundleModules(DubBundle bundle) {
		ArrayList<Path> importFolders = bundle.getEffectiveImportFolders_AbsolutePath();
		
		for (Path importFolder : importFolders) {
			visitImportFolder(importFolder);
		}
	}
	
	protected void visitImportFolder(final Path importFolder) {
		try {
			Files.walkFileTree(importFolder, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
					if(dir == importFolder) {
						return FileVisitResult.CONTINUE;
					}
					
					assertTrue(dir.startsWith(importFolder));
					Path relPath = importFolder.relativize(dir);
					if(ModuleNamingRules.isValidPackagePath(relPath)) {
						return FileVisitResult.CONTINUE;
					}
					return FileVisitResult.SKIP_SUBTREE;
				}
				
				@Override
				public FileVisitResult visitFile(Path filePath, BasicFileAttributes attrs) throws IOException {
					visitPotentialModuleFile(filePath, importFolder);
					
					return FileVisitResult.CONTINUE;
				}

			});
		} catch (IOException e) {
			dtoolServer.logError("Could not read file or dir: " , e);
		}
	}
	
	protected void visitPotentialModuleFile(Path fullPath, Path importFolder) {
		assertTrue(fullPath.isAbsolute());
		Path relPath = importFolder.relativize(fullPath);
		ModuleFullName moduleFullName = ModuleNamingRules.getValidModuleFullNameOrNull(relPath);
		if(moduleFullName != null) {
			assertTrue(fullPath.isAbsolute());
			addModuleEntry(moduleFullName, fullPath);
		}
	}
	
	protected void addModuleEntry(ModuleFullName moduleFullName, Path fullPath) {
		modules.put(moduleFullName, fullPath);
		moduleFiles.add(fullPath);
	}
	
}