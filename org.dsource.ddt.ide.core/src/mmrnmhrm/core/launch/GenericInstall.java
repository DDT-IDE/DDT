package mmrnmhrm.core.launch;



public class GenericInstall extends CommonDeeInstall { 
	
	public GenericInstall(GenericInstallType type, String id) {
		super(type, id);
	}
	
	@Override
	public String getDefaultBuildFileData() {
		return 
			"//Unsupported\n"+	
			"$/DEEBUILDER.SRCMODULES\n"
		;
	}
	
	@Override
	public String getDefaultBuildToolCmdLine() {
		return "$DEEBUILDER.COMPILEREXEPATH @build.rf";
	}
	
}
