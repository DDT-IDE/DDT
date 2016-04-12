/*******************************************************************************
 * Copyright (c) 2016 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.lang.ide.core.operations.build;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertAreEqual;
import static melnorme.utilbox.core.CoreUtil.areEqual;

import melnorme.utilbox.misc.HashcodeUtil;

public class BuildTargetData implements BuildTargetDataView {
	
	public String targetName;
	public boolean normalBuildEnabled;
	public boolean autoBuildEnabled;
	public String buildArguments;
	public String executablePath;
	
	public BuildTargetData() {
	}
	
	public BuildTargetData(String targetName, boolean normalBuildEnabled, boolean autoBuildEnabled) {
		this.targetName = targetName;
		this.normalBuildEnabled = normalBuildEnabled;
		this.autoBuildEnabled = autoBuildEnabled;
	}
	
	public BuildTargetData(String targetName, boolean normalBuildEnabled, boolean autoBuildEnabled, 
			String buildArguments, String executablePath) {
		this(targetName, normalBuildEnabled, autoBuildEnabled);
		
		this.buildArguments = buildArguments;
		this.executablePath = executablePath;
	}
	
	public BuildTargetData(BuildTargetDataView data) {
		this(
			data.getTargetName(),
			data.isNormalBuildEnabled(),
			data.isAutoBuildEnabled(),
			data.getBuildArguments(),
			data.getExecutablePath()
		);
	}
	
	public BuildTargetData setData(BuildTargetDataView data) {
		this.targetName = data.getTargetName();
		this.normalBuildEnabled = data.isNormalBuildEnabled(); 
		this.autoBuildEnabled = data.isAutoBuildEnabled();
		this.buildArguments = data.getBuildArguments();
		this.executablePath = data.getExecutablePath();
		
		assertAreEqual(this, data);
		return this;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(!(obj instanceof BuildTargetData)) return false;
		
		BuildTargetData other = (BuildTargetData) obj;
		
		return 
				areEqual(targetName, other.targetName) &&
				areEqual(normalBuildEnabled, other.normalBuildEnabled) &&
				areEqual(autoBuildEnabled, other.autoBuildEnabled) &&
				areEqual(buildArguments, other.buildArguments) &&
				areEqual(executablePath, other.executablePath);
	}
	
	@Override
	public int hashCode() {
		return HashcodeUtil.combinedHashCode(targetName, normalBuildEnabled, autoBuildEnabled, buildArguments);
	}
	
	/* -----------------  ----------------- */
	
	@Override
	public String getTargetName() {
		return targetName;
	}
	
	@Override
	public boolean isNormalBuildEnabled() {
		return normalBuildEnabled;
	}
	
	@Override
	public boolean isAutoBuildEnabled() {
		return autoBuildEnabled;
	}
	
	@Override
	public String getBuildArguments() {
		return buildArguments;
	}
	
	@Override
	public String getExecutablePath() {
		return executablePath;
	}
	
}