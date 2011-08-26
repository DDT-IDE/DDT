package mmrnmhrm.core.build;

import mmrnmhrm.core.launch.DmdInstall;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

public class DeeBuildOptions {
	
	public static enum EBuildTypes {
		EXECUTABLE,
		LIB_STATIC,
		LIB_DYNAMIC
	}
	
	public EBuildTypes buildType;
	public String artifactName;
	/** Project relative path */
	public IPath outputDir;
	public String buildToolCmdLine;
	public String buildCommands;
	
	public DeeBuildOptions(String projname, DmdInstall deeInstall) {
		buildType = EBuildTypes.EXECUTABLE;
		artifactName = projname + getOSExtension();
		outputDir = new Path(defaultOutputFolder());
		buildToolCmdLine = deeInstall == null ? "" : deeInstall.getDefaultBuildToolCmdLine();
		buildCommands = deeInstall == null ? "" : deeInstall.getDefaultBuildFileData();
	}
	
	/** copy constructor */
	protected DeeBuildOptions(DeeBuildOptions other) {
		buildType = other.buildType;
		artifactName = other.artifactName;
		outputDir = other.outputDir;
		buildToolCmdLine = other.buildToolCmdLine;
		buildCommands = other.buildCommands;
	}
	
	protected String defaultOutputFolder() {
		return "bin";
	}
	
	@Override
	public DeeBuildOptions clone() {
		return new DeeBuildOptions(this);
	}
	
	protected static String getOSExtension() {
		if(Platform.getOS().equals(Platform.OS_WIN32))
			return ".exe";
		return "";
	}
	
}
