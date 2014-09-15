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
package dtool.engine.compiler_installs;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.CoreUtil.areEqual;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import dtool.util.NewUtils;

public class CompilerInstall {
	
	public static enum ECompilerType {
		DMD, GDC, LDC, OTHER
	}
	
	protected final Path compilerPath;
	protected final ECompilerType compilerType;
	protected final List<Path> librarySourceFolders;
	
	
	public CompilerInstall(Path compilerPath, ECompilerType compilerType, Path... librarySourceFolders) {
		this(compilerPath, compilerType, NewUtils.normalizePaths(librarySourceFolders));
	}
	
	public CompilerInstall(Path compilerPath, ECompilerType compilerType, List<Path> librarySourceFolders) {
		this.compilerPath = compilerPath.normalize();
		this.compilerType = compilerType;
		this.librarySourceFolders = Collections.unmodifiableList(librarySourceFolders);
		for (Path path : librarySourceFolders) {
			assertTrue(path.isAbsolute() && path.equals(path.normalize()));
		}
	}
	
	public Path getCompilerPath() {
		return compilerPath;
	}
	
	public CompilerInstall.ECompilerType getCompilerType() {
		return compilerType;
	}
	
	public List<Path> getLibrarySourceFolders() {
		return librarySourceFolders;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(!(obj instanceof CompilerInstall)) return false;
		
		CompilerInstall other = (CompilerInstall) obj;
		
		return areEqual(compilerPath, other.compilerPath) &&
				areEqual(librarySourceFolders, other.librarySourceFolders);
	}
	
	@Override
	public int hashCode() {
		return compilerPath.hashCode();
	}
	
}