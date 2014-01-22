package melnorme.ide.launching;

import java.util.Map;

import org.eclipse.core.runtime.IPath;

public class ProcessSpawnInfo {
	
	public IPath programPath;
	public String[] programArguments;
	public IPath workingDir;
	public Map<String, String> environment;
	public boolean appendEnv;
	
	public ProcessSpawnInfo(IPath programPath, String[] programArguments, IPath workingDir, 
			Map<String, String> environment, boolean appendEnv) {
		this.programPath = programPath;
		this.programArguments = programArguments;
		this.workingDir = workingDir;
		this.environment = environment;
		this.appendEnv = appendEnv;
	}
	
}