package mmrnmhrm.core.launch;

import org.dsource.ddt.lang.core.launch.AbstractInterpreterRunner_LangExtension;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.internal.launching.InterpreterMessages;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.InterpreterConfig;
import org.eclipse.osgi.util.NLS;

public class DeeNativeRunner extends AbstractInterpreterRunner_LangExtension {
	
	protected DeeNativeRunner(IInterpreterInstall install) {
		super(install);
	}
	
	@Override
	protected String getProcessType() {
		return DeeLaunchConfigurationConstants.ID_DEE_PROCESS_TYPE;
	}
	
	@Override
	protected void checkConfig(InterpreterConfig config, IEnvironment environment) throws CoreException {
		IPath workingDirectoryPath = config.getWorkingDirectoryPath();
		IFileHandle dir = environment.getFile(workingDirectoryPath);
		if (!dir.exists()) {
			abort(NLS.bind(
					InterpreterMessages.errDebuggingEngineWorkingDirectoryDoesntExist, dir.toString()), null);
		}
		if(!config.isNoFile()) {
			return;
		}
		if (config.getScriptFilePath() == null) {
			abort(LaunchMessages.errDebuggingEngineExecutableFileDoesntExist, null);
		}
		final IFileHandle script = environment.getFile(config.getScriptFilePath());
		if(!script.exists()) {
			abort(NLS.bind(
					LaunchMessages.errDebuggingEngineExecutableFileDoesntExist, script.toString()), null);
		}
	}
	
	@Override
	protected String[] renderCommandLine(InterpreterConfig config) {
		return renderCommandLineForCompiledExecutable(config);
	}
	
	@Override
	protected IProcess rawRun(ILaunch launch, InterpreterConfig config) throws CoreException {
		return super.rawRun(launch, config);
	}
	
}