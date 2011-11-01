package mmrnmhrm.core.launch;


import org.eclipse.core.runtime.IPath;

public class GDCInstall extends CommonDeeInstall {
	
	public GDCInstall(GDCInstallType type, String id) {
		super(type, id);
	}
	
	/** Get the path of the compiler directory */
	public IPath getCompilerBasePath() {
		return getInstallLocation().getPath().removeLastSegments(1);
	}
	
	/** Get the full path of the compiler, including the executable */
	public IPath getCompilerFullPath() {
		return getInstallLocation().getPath();
	}
	
	@Override
	public String getDefaultBuildFileData() {
		return 
			"-v2\n" +
			"-o$/DEEBUILDER.OUTPUTEXE\n" +
			"$/DEEBUILDER.SRCLIBS.-I\n" +
			"$/DEEBUILDER.SRCFOLDERS.-I\n" +
			"$/DEEBUILDER.SRCMODULES\n"
		;
	}
	
	@Override
	public String getDefaultBuildToolCmdLine() {
		return "$DEEBUILDER.COMPILEREXEPATH @build.rf";
	}
	
}
