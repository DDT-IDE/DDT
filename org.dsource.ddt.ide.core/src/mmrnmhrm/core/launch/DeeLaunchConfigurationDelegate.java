package mmrnmhrm.core.launch;



import org.dsource.ddt.ide.core.DeeNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.dltk.launching.AbstractScriptLaunchConfigurationDelegate;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.IInterpreterRunner;
import org.eclipse.dltk.launching.InterpreterConfig;
import org.eclipse.dltk.launching.LaunchingMessages;
import org.eclipse.dltk.launching.ScriptLaunchConfigurationConstants;

public class DeeLaunchConfigurationDelegate extends AbstractScriptLaunchConfigurationDelegate {
	
	@Override
	public String getLanguageId() {
		return DeeNature.NATURE_ID;
	}
	
	@Override
	protected InterpreterConfig createInterpreterConfig(ILaunchConfiguration configuration, ILaunch launch)
			throws CoreException {
		return super.createInterpreterConfig(configuration, launch);
	}
	
	@Override
	public boolean buildForLaunch(ILaunchConfiguration configuration, String mode, IProgressMonitor monitor)
			throws CoreException {
		return super.buildForLaunch(configuration, mode, monitor);
	}
	
	@Override
	public IInterpreterRunner getInterpreterRunner(ILaunchConfiguration configuration, String mode)
			throws CoreException {
		
		IInterpreterInstall interpreter = getInterpreterInstall(configuration);
		if (interpreter == null) {
			throw abort(
					LaunchingMessages.AbstractScriptLaunchConfigurationDelegate_The_specified_InterpreterEnvironment_installation_does_not_exist_4,
					null,
					ScriptLaunchConfigurationConstants.ERR_INTERPRETER_INSTALL_DOES_NOT_EXIST);
		}
		
		return new DeeNativeRunner(interpreter);
	}
	
	@Override
	protected void runRunner(ILaunchConfiguration configuration, IInterpreterRunner runner, InterpreterConfig config,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		super.runRunner(configuration, runner, config, launch, monitor);
	}
	
}
