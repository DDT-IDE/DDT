package mmrnmhrm.core.compiler_installs;



public class GDCInstall extends CommonDeeInstall { 
	
	public GDCInstall(GDCInstallType type, String id) {
		super(type, id);
	}
	
	@Override
	public String getDefaultBuildFileData() {
		return 
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
