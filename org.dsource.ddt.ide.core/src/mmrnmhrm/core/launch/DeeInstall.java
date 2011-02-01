package mmrnmhrm.core.launch;

import mmrnmhrm.core.model.DeeNature;

import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.launching.AbstractInterpreterInstall;
import org.eclipse.dltk.launching.IInterpreterInstallType;
import org.eclipse.dltk.launching.IInterpreterRunner;

public class DeeInstall extends AbstractInterpreterInstall {
	
	public DeeInstall(IInterpreterInstallType type, String id) {
		super(type, id);
	}
	
	@Override
	public String getNatureId() {
		return DeeNature.NATURE_ID;
	}
	
	@Override
	public void setInstallLocation(IFileHandle installLocation) {
		super.setInstallLocation(installLocation);
		if(getName() == null) {
		}
	}
	
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
	
	/** Get the path of the compiler directory */
	public IPath getCompilerBasePath() {
		return getInstallLocation().getPath().removeLastSegments(1);
	}
	
	/** Get the full path of the compiler, including the executable */
	public IPath getCompilerFullPath() {
		return getInstallLocation().getPath();
	}
	
}
