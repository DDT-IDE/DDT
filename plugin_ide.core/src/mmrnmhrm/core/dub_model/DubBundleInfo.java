/*******************************************************************************
 * Copyright (c) 2014 Bruno Medeiros and other Contributors.
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
import static melnorme.utilbox.misc.StringUtil.asString;

import org.eclipse.core.resources.IProject;

import dtool.dub.DubBundle;
import dtool.dub.DubBundle.DubConfiguration;
import dtool.dub.DubBundleDescription;
import dtool.engine.compiler_installs.CompilerInstall;
import melnorme.lang.ide.core.operations.build.BuildManager.BuildConfiguration;
import melnorme.lang.ide.core.project_model.AbstractBundleInfo;
import melnorme.utilbox.collections.ArrayList2;
import melnorme.utilbox.collections.Indexable;
import melnorme.utilbox.core.CommonException;
import mmrnmhrm.core.workspace.viewmodel.DubDependenciesContainer;

public class DubBundleInfo extends AbstractBundleInfo {
	
	public static final String DEFAULT_CONFIGURATION = "(default)";
	
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
	public Indexable<BuildConfiguration> getBuildConfigurations() {
		DubBundle mainBundle = getMainBundle();
		
		ArrayList2<BuildConfiguration> buildConfigs = ArrayList2.create();
		
		for(DubConfiguration config : mainBundle.getConfigurations()) {
			buildConfigs.add(
				new BuildConfiguration(config.name, null) {
					@Override
					public String getArtifactPath() throws CommonException {
						return asString(config.getEffectiveTargetFullPath(mainBundle));
					};
				}
			);
		}
		
		if(buildConfigs.isEmpty()) {
			buildConfigs.add(
				new BuildConfiguration(DEFAULT_CONFIGURATION, null) {
					@Override
					public String getArtifactPath() throws CommonException {
						DubConfiguration defaulConfig = new DubConfiguration("", null, null, null);
						return asString(defaulConfig.getEffectiveTargetFullPath(mainBundle));
					}
				}
			);
		}
		
		return buildConfigs;
	}
	
}