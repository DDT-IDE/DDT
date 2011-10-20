package mmrnmhrm.core.launch;


import org.dsource.ddt.ide.core.DeeNature;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.dltk.launching.AbstractInterpreterInstall;
import org.eclipse.dltk.launching.IInterpreterInstallType;
import org.eclipse.dltk.launching.IInterpreterRunner;

public abstract class CommonDeeInstall extends AbstractInterpreterInstall {
	
	public CommonDeeInstall(IInterpreterInstallType type, String id) {
		super(type, id);
	}
	
	@Override
	public String getNatureId() {
		return DeeNature.NATURE_ID;
	}
	
	/** Get the path of the compiler directory */
	public IPath getCompilerDirectoryPath() {
		return getInstallLocation().getPath().removeLastSegments(1);
	}
	
	/** Get the path of the compiler executable */
	public IPath getCompilerExecutablePath() {
		return getInstallLocation().getPath();
	}
	
	public abstract String getDefaultBuildFileData();
	
	public abstract String getDefaultBuildToolCmdLine() ;
	
	@Override
	public IInterpreterRunner getInterpreterRunner(String mode) {
		IInterpreterRunner runner = super.getInterpreterRunner(mode);
		if (runner != null) {
			return runner;
		}
		
		if (mode.equals(ILaunchManager.RUN_MODE)) {
			return new DeeInterpreterRunner(this);
		}
		
		return null;
	}
	
}