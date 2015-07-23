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
package mmrnmhrm.core.dub_model;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.nio.file.Path;

import org.eclipse.core.resources.IProject;

import dtool.dub.DubBundle;
import dtool.dub.DubBundleDescription;
import dtool.engine.compiler_installs.CompilerInstall;
import melnorme.lang.ide.core.project_model.AbstractBundleInfo;
import melnorme.utilbox.collections.ArrayList2;
import melnorme.utilbox.collections.Indexable;
import melnorme.utilbox.core.CommonException;
import mmrnmhrm.core.workspace.viewmodel.DubDependenciesContainer;

public class DubBundleInfo extends AbstractBundleInfo {
	
	protected final CompilerInstall compilerInstall; 
	protected final DubBundleDescription bundleDesc;
	
	public DubBundleInfo(CompilerInstall compilerInstall, DubBundleDescription bundleDesc) {
		this.compilerInstall = assertNotNull(compilerInstall);
		this.bundleDesc = assertNotNull(bundleDesc);
	}
	
	public CompilerInstall getCompilerInstall() {
		return compilerInstall;
	}
	
	public DubBundleDescription getBundleDesc() {
		return bundleDesc;
	}
	
	public DubBundle getMainBundle() {
		return getBundleDesc().getMainBundle();
	}
	
	public DubDependenciesContainer getDubContainer(IProject project) {
		DubBundleDescription bundleInfo = getBundleDesc();
		return new DubDependenciesContainer(bundleInfo, project);
	}
	
	@Override
	public Path getEffectiveTargetFullPath() throws CommonException {
		return getBundleDesc().getMainBundle().getEffectiveTargetFullPath();
	}
	
	@Override
	public Indexable<String> getBuildConfigurations() {
		return ArrayList2.create("");
//		return ArrayList2.create(
//			new BuildConfiguration(null, null) {
//				@Override
//				public Path getArtifactPath() throws CommonException {
//					return getEffectiveTargetFullPath();
//				};
//			}
//		);
	}
	
}