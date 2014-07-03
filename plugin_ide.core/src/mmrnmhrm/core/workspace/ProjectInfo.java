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
package mmrnmhrm.core.workspace;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import mmrnmhrm.core.workspace.viewmodel.DubDependenciesContainer;

import org.eclipse.core.resources.IProject;

import dtool.dub.DubBundleDescription;
import dtool.engine.compiler_installs.CompilerInstall;

public class ProjectInfo {
	
	protected final CompilerInstall compilerInstall; 
	protected final DubBundleDescription bundleDesc;
	
	public ProjectInfo(CompilerInstall compilerInstall, DubBundleDescription bundleDesc) {
		this.compilerInstall = assertNotNull(compilerInstall);
		this.bundleDesc = assertNotNull(bundleDesc);
	}
	
	public CompilerInstall getCompilerInstall() {
		return compilerInstall;
	}
	
	public DubBundleDescription getBundleDesc() {
		return bundleDesc;
	}
	
	public DubDependenciesContainer getDubContainer(IProject project) {
		DubBundleDescription bundleInfo = getBundleDesc();
		return new DubDependenciesContainer(bundleInfo, project);
	}
	
}