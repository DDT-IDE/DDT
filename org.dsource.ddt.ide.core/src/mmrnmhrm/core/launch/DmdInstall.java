package mmrnmhrm.core.launch;


import org.eclipse.dltk.launching.IInterpreterInstallType;

public class DmdInstall extends CommonDeeInstall {
	
	public DmdInstall(IInterpreterInstallType type, String id) {
		super(type, id);
	}
	
	@Override
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
	
	@Override
	public String getDefaultBuildToolCmdLine() {
		return "$DEEBUILDER.COMPILEREXEPATH @build.rf";
	}
	
}
