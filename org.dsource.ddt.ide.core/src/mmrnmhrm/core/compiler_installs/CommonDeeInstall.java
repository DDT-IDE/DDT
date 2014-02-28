package mmrnmhrm.core.compiler_installs;


import org.dsource.ddt.ide.core.DeeNature;
import org.eclipse.core.runtime.IPath;
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
	
	@Override
	public IInterpreterRunner getInterpreterRunner(String mode) {
		return null; // No need for one, the D LaunchConfigurationDelegate know how to launch things
	}
	
}