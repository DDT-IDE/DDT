package mmrnmhrm.core.build;

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
	
	public DeeBuildOptions(String projname) {
		buildType = EBuildTypes.EXECUTABLE;
		artifactName = projname + getOSExtension();
		outputDir = new Path(defaultOutputFolder());
		buildToolCmdLine = DeeBuilder.getDefaultBuildToolCmdLine();
		buildCommands = DeeBuilder.getDefaultBuildFileData();
	}

	private static String getOSExtension() {
		if(Platform.getOS().equals(Platform.OS_WIN32))
			return ".exe";
		return "";
	}

	private String defaultOutputFolder() {
		return "bin";
	}
	
	@Override
	public DeeBuildOptions clone() {
		DeeBuildOptions options = new DeeBuildOptions(artifactName);
		options.buildType = buildType;
		options.artifactName = artifactName;
		options.outputDir = outputDir;
		options.buildToolCmdLine = buildToolCmdLine;
		options.buildCommands = buildCommands;

		return options;
	}
}
