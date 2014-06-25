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

import java.nio.file.Path;
import java.util.List;

import dtool.engine.compiler_installs.CompilerInstall;
import dtool.engine.compiler_installs.CompilerInstall.ECompilerType;
import dtool.engine.modules.IModuleResolver;

public class StandardLibraryResolution extends AbstractBundleResolution implements IModuleResolver {
	
	protected CompilerInstall compilerInstall;
	
	public StandardLibraryResolution(SemanticManager manager, CompilerInstall compilerInstall) {
		super(manager, compilerInstall.getLibrarySourceFolders());
		this.compilerInstall = compilerInstall;
	}
	
	public CompilerInstall getCompilerInstall() {
		return compilerInstall;
	}
	
	public ECompilerType getCompilerType() {
		return compilerInstall.getCompilerType();
	}
	
	public List<Path> getLibrarySourceFolders() {
		return compilerInstall.getLibrarySourceFolders();
	}
	
}