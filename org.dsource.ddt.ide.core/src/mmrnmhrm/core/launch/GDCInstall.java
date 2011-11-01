package mmrnmhrm.core.launch;



public class GDCInstall extends CommonDeeInstall { 
	
	public GDCInstall(GDCInstallType type, String id) {
		super(type, id);
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
