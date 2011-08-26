package mmrnmhrm.core.launch;


import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.launching.IInterpreterInstallType;

public class DmdInstall extends CommonDeeInstall {
	
	public DmdInstall(IInterpreterInstallType type, String id) {
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
	
	public String getDefaultBuildFileData() {
		return 
			"-od$DEEBUILDER.OUTPUTPATH\n" +
			"-of$DEEBUILDER.OUTPUTEXE\n" +
			//"$DEEBUILDER.EXTRAOPTS\n" +
			"$DEEBUILDER.SRCLIBS.-I\n" +
			"$DEEBUILDER.SRCFOLDERS.-I\n" +
			"$DEEBUILDER.SRCMODULES\n"
		;
	}
	
	public String getDefaultBuildToolCmdLine() {
		return "$DEEBUILDER.COMPILEREXEPATH @build.rf";
	}
	
}
