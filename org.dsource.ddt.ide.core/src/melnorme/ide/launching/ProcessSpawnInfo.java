package melnorme.ide.launching;

import java.util.Map;

import org.eclipse.core.runtime.IPath;

public class ProcessSpawnInfo {
	
	public IPath workingDir;
	public IPath processFile;
	public String[] processArguments;
	public Map<String, String> environment;
	public boolean appendEnv;
	
	public ProcessSpawnInfo(IPath processFile, String[] processArguments, IPath workingDir, 
			Map<String, String> environment, boolean appendEnv) {
		this.workingDir = workingDir;
		this.processFile = processFile;
		this.processArguments = processArguments;
		this.environment = environment;
		this.appendEnv = appendEnv;
	}
	
}