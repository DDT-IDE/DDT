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
import static melnorme.utilbox.misc.CollectionUtil.createArrayList;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class CompilerInstall {
	
	public static enum ECompilerType {
		DMD, GDC, LDC, OTHER
	}
	
	public static CompilerInstall create(ECompilerType compilerType, Path... librarySourceFolders) {
		for (int i = 0; i < librarySourceFolders.length; i++) {
			librarySourceFolders[i] = librarySourceFolders[i].normalize();
		}
		return new CompilerInstall(compilerType, createArrayList(librarySourceFolders));
	}
	
	protected final ECompilerType compilerType;
	protected final List<Path> librarySourceFolders;
	
	protected CompilerInstall(CompilerInstall.ECompilerType compilerType, List<Path> librarySourceFolders) {
		this.compilerType = compilerType;
		this.librarySourceFolders = Collections.unmodifiableList(librarySourceFolders);
		for (Path path : librarySourceFolders) {
			assertTrue(path.isAbsolute() && path.equals(path.normalize()));
		}
	}
	
	public CompilerInstall.ECompilerType getCompilerType() {
		return compilerType;
	}
	
	public List<Path> getLibrarySourceFolders() {
		return librarySourceFolders;
	}
	
}