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
package dtool.engine.modules;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import melnorme.lang.tooling.bundles.ModuleFullName;
import dtool.engine.BundleModules;

public abstract class BundleModulesVisitor {
	
	protected final HashMap<ModuleFullName, Path> modules = new HashMap<>();
	protected final HashSet<Path> moduleFiles = new HashSet<>();
	protected final List<Path> importFolders;
	
	public BundleModulesVisitor(List<Path> importFolders) {
		this.importFolders = importFolders;
		visitBundleModules(importFolders);
	}
	
	public void visitBundleModules(List<Path> importFolders) {
		for (Path importFolder : importFolders) {
			try {
				visitImportFolder(importFolder);
			} catch (IOException e) {
				throw assertFail("Should not happen, file visit should not throw exception");
			}
		}
	}
	
	protected void visitImportFolder(final Path importFolder) throws IOException {
		if(!importFolder.toFile().exists()) {
			return;
		}
		Files.walkFileTree(importFolder, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				if(dir == importFolder) {
					return FileVisitResult.CONTINUE;
				}
				
				assertTrue(dir.startsWith(importFolder));
				Path relPath = importFolder.relativize(dir);
				if(ModuleNamingRules.isValidPackageNameSegment(relPath.getFileName().toString())) {
					return FileVisitResult.CONTINUE;
				}
				return FileVisitResult.SKIP_SUBTREE;
			}
			
			@Override
			public FileVisitResult visitFile(Path filePath, BasicFileAttributes attrs) throws IOException {
				visitPotentialModuleFile(filePath, importFolder);
				
				return FileVisitResult.CONTINUE;
			}
			
			@Override
			public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
				return handleFileVisitException(file, exc);
			}
			
		});
	}
	
	protected void visitPotentialModuleFile(Path fullPath, Path importFolder) {
		assertTrue(fullPath.isAbsolute());
		Path relPath = importFolder.relativize(fullPath);
		ModuleFullName moduleFullName = ModuleNamingRules.getValidModuleNameOrNull(relPath);
		if(moduleFullName != null) {
			assertTrue(fullPath.isAbsolute());
			addModuleEntry(moduleFullName, fullPath);
		}
	}
	
	protected abstract FileVisitResult handleFileVisitException(Path file, IOException exc);
	
	protected void addModuleEntry(ModuleFullName moduleFullName, Path fullPath) {
		modules.put(moduleFullName, fullPath);
		moduleFiles.add(fullPath);
	}
	
	public HashSet<Path> getModuleFiles() {
		return moduleFiles;
	}
	
	public BundleModules toBundleModules() {
		return new BundleModules(modules, moduleFiles, importFolders);
	}
	
}