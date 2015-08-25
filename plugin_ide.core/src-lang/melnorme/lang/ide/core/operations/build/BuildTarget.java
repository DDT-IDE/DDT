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

import static melnorme.utilbox.core.CoreUtil.areEqual;

import melnorme.utilbox.misc.HashcodeUtil;
import melnorme.utilbox.misc.StringUtil;

public class BuildTarget {
	
	public static class BuildTargetData {
		
		public String targetName;
		public boolean enabled;
		public String buildArguments;
		public String executablePath;
		
		public BuildTargetData() {
		}
		
		public BuildTargetData(String targetName, boolean enabled, String buildArguments, String executablePath) {
			this.targetName = targetName;
			this.enabled = enabled;
			this.buildArguments = buildArguments;
			this.executablePath = executablePath;
		}
		
		@Override
		public boolean equals(Object obj) {
			if(this == obj) return true;
			if(!(obj instanceof BuildTargetData)) return false;
			
			BuildTargetData other = (BuildTargetData) obj;
			
			return 
					areEqual(targetName, other.targetName) &&
					areEqual(enabled, other.enabled) &&
					areEqual(buildArguments, other.buildArguments) &&
					areEqual(executablePath, other.executablePath);
		}
		
		@Override
		public int hashCode() {
			return HashcodeUtil.combinedHashCode(targetName, enabled, buildArguments);
		}
		
	}
	
	protected final BuildTargetData data = new BuildTargetData();
	
	public BuildTarget(BuildTargetData data) {
		this(data.targetName, data.enabled, data.buildArguments, data.executablePath);
	}
	
	public BuildTarget(String targetName, boolean enabled, String buildArguments, String executablePath) {
		data.targetName = StringUtil.nullAsEmpty(targetName);
		data.enabled = enabled;
		data.buildArguments = buildArguments;
		data.executablePath = executablePath;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(!(obj instanceof BuildTarget)) return false;
		
		BuildTarget other = (BuildTarget) obj;
		
		return areEqual(data, other.data);
	}
	
	@Override
	public int hashCode() {
		return HashcodeUtil.combinedHashCode(data);
	}
	
	@Override
	public String toString() {
		return data.targetName + (data.enabled ? " [ENABLED]" : "");
	}
	
	/* -----------------  ----------------- */
	
	public boolean isEnabled() {
		return data.enabled;
	}
	
	public String getBuildArguments() {
		return data.buildArguments;
	}
	
	public String getTargetName() {
		return data.targetName;
	}
	
	public String getExecutablePath() {
		return data.executablePath;
	}
	
	public BuildTargetData getDataCopy() {
		return new BuildTargetData(
			getTargetName(),
			isEnabled(),
			getBuildArguments(),
			getExecutablePath()
		);
	}
	
}