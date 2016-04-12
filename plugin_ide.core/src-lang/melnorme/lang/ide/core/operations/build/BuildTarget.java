/*******************************************************************************
 * Copyright (c) 2015 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.lang.ide.core.operations.build;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.nio.file.Path;

import org.eclipse.core.resources.IProject;

import melnorme.lang.ide.core.BundleInfo;
import melnorme.lang.ide.core.LangCore;
import melnorme.lang.ide.core.launch.LaunchMessages;
import melnorme.lang.ide.core.operations.ToolManager;
import melnorme.lang.ide.core.operations.ILangOperationsListener_Default.IOperationConsoleHandler;
import melnorme.lang.ide.core.operations.build.BuildManager.BuildType;
import melnorme.lang.ide.core.utils.ResourceUtils;
import melnorme.lang.tooling.bundle.BuildConfiguration;
import melnorme.lang.tooling.bundle.LaunchArtifact;
import melnorme.lang.tooling.data.AbstractValidator;
import melnorme.lang.tooling.ops.util.ValidationMessages;
import melnorme.utilbox.collections.Indexable;
import melnorme.utilbox.core.CommonException;
import melnorme.utilbox.misc.Location;

public class BuildTarget extends AbstractValidator {
	
	public final BuildManager buildMgr = LangCore.getBuildManager();
	
	protected final IProject project;
	protected final BundleInfo bundleInfo;
	protected final BuildTargetDataView targetData;
	protected final BuildType buildType;
	protected final BuildConfiguration buildConfiguration;
	
	public static BuildTarget create(IProject project, BundleInfo bundleInfo, BuildTargetDataView buildTargetData,
			BuildType buildType, String buildConfigName) throws CommonException {
		BuildConfiguration buildConfig = buildType.getValidBuildconfiguration(buildConfigName, bundleInfo);
		return new BuildTarget(project, bundleInfo, buildTargetData, buildType, buildConfig);
	}
	
	public BuildTarget(IProject project, BundleInfo bundleInfo, 
			BuildTargetDataView buildTargetData, BuildType buildType, 
			BuildConfiguration buildConfig) {
		this.project = assertNotNull(project);
		this.bundleInfo = assertNotNull(bundleInfo);
		
		this.targetData = assertNotNull(buildTargetData);
		assertNotNull(buildTargetData.getTargetName());
		this.buildType = assertNotNull(buildType);
		
		this.buildConfiguration = assertNotNull(buildConfig);
	}
	
	public BuildManager getBuildManager() {
		return buildMgr;
	}
	
	public IProject getProject() {
		return project;
	}
	
	@Override
	public String toString() {
		return project.getName() + " >> " + targetData.getTargetName();
	}
	
	public Location getProjectLocation() throws CommonException {
		return ResourceUtils.getProjectLocation2(project);
	}
	
	public String getBuildTargetName() {
		return targetData.getTargetName();
	}
	
	public String getTargetName() {
		return assertNotNull(targetData.getTargetName());
	}
	
	public boolean isNormalBuildEnabled() {
		return targetData.isNormalBuildEnabled();
	}
	
	public boolean isAutoBuildEnabled() {
		return targetData.isAutoBuildEnabled();
	}
	
	public BuildTargetDataView getData() {
		return targetData;
	}
	
	public BuildTargetData getDataCopy() {
		return new BuildTargetData(targetData);
	}
	
	public String getBuildConfigName() {
		return buildConfiguration.getName();
	}
	
	public String getBuildTypeName() {
		return buildType.getName();
	}
	
	public BuildConfiguration getBuildConfiguration() {
		return buildConfiguration;
	}
	
	public BuildType getBuildType() {
		return buildType;
	}
	
	public boolean isDefaultBuildType() {
		return getBuildType() == buildMgr.getDefaultBuildType();
	}
	
	public BundleInfo getBundleInfo() {
		return bundleInfo;
	}
	
	/* -----------------  ----------------- */
	
	public String getDefaultBuildArguments() throws CommonException {
		return getBuildType().getDefaultBuildArguments(this);
	}
	
	public String getEffectiveBuildArguments() throws CommonException {
		String buildOptions = targetData.getBuildArguments();
		if(buildOptions != null) {
			return buildOptions;
		}
		return getDefaultBuildArguments();
	}
	
	/* -----------------  ----------------- */
	
	public String getEffectiveValidExecutablePath() throws CommonException {
		String executablePath = targetData.getExecutablePath();
		if(executablePath != null) {
			return executablePath;
		}
		
		return getDefaultExecutablePath();
	}
	
	public String getDefaultExecutablePath() throws CommonException {
		LaunchArtifact mainLaunchArtifact = getMainLaunchArtifact();
		if(mainLaunchArtifact == null) {
			throw new CommonException(LaunchMessages.MSG_BuildTarget_NoExecutableAvailable());
		}
		return mainLaunchArtifact.getArtifactPath();
	}
	
	public Indexable<LaunchArtifact> getSubLaunchArtifacts() throws CommonException {
		return getBuildType().getSubTargetLaunchArtifacts(this);
	}
	
	public LaunchArtifact getMainLaunchArtifact() throws CommonException {
		return getBuildType().getMainLaunchArtifact(this);
	}
	
	public Location getValidExecutableLocation() throws CommonException {
		return getValidExecutableLocation2(getEffectiveValidExecutablePath());
	}
	
	public Location getValidExecutableLocation2(String exeFilePathString) throws CommonException {
		if(exeFilePathString == null || exeFilePathString.isEmpty()) {
			throw new CommonException(LaunchMessages.BuildTarget_NoArtifactPathSpecified);
		}
		
		Location exeFileLocation = Location.create(getProjectLocation(), exeFilePathString);
		
		if(exeFileLocation.toFile().exists() && !exeFileLocation.toFile().isFile()) {
			error(ValidationMessages.Location_NotAFile(exeFileLocation));
		}
		return exeFileLocation;
	}
	
	/* -----------------  ----------------- */
	
	public void validateForBuild() throws CommonException {
		getEffectiveBuildArguments();
		getValidExecutableLocation();
	}
	
	public CommonBuildTargetOperation getBuildOperation(IOperationConsoleHandler opHandler)
			throws CommonException {
		ToolManager toolManager = LangCore.getToolManager();
		
		Path buildToolPath = toolManager.getSDKToolPath(getProject());
		return getBuildOperation(opHandler, buildToolPath);
	}
	
	public CommonBuildTargetOperation getBuildOperation(IOperationConsoleHandler opHandler, Path buildToolPath) 
			throws CommonException {
		assertNotNull(opHandler);
		String buildArguments = getEffectiveBuildArguments();
		return getBuildType().getBuildOperation(this, opHandler, buildToolPath, buildArguments);
	}
	
}