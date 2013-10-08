package melnorme.lang.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.internal.launching.InterpreterMessages;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.IInterpreterRunner;
import org.eclipse.dltk.launching.InterpreterConfig;
import org.eclipse.osgi.util.NLS;

/**
 * Abstract implementation of a interpreter runner.
 * <p>
 * Clients implementing interpreter runners should subclass this class.
 * </p>
 * 
 * @see IInterpreterRunner
 * 
 */
public abstract class AbstractInterpreterRunner extends AbstractInterpreterRunner_Mod  {
	
	protected IInterpreterInstall interpreterInstall;
	
	protected AbstractInterpreterRunner(IInterpreterInstall install) {
		this.interpreterInstall = install;
	}
	
	protected IInterpreterInstall getInstall() {
		return interpreterInstall;
	}
	
	@Override
	protected void checkConfig(InterpreterConfig config) throws CoreException {
		IEnvironment environment = getInstall().getEnvironment();
		
		IPath workingDirectoryPath = config.getWorkingDirectoryPath();
		IFileHandle dir = environment.getFile(workingDirectoryPath);
		if (!dir.exists()) {
			abort(
					NLS
							.bind(
									InterpreterMessages.errDebuggingEngineWorkingDirectoryDoesntExist,
									dir.toString()), null);
		}
		if (config.getScriptFilePath() == null) {
			return;
		}
		if (!config.isNoFile()) {
			final IFileHandle script = environment.getFile(config
					.getScriptFilePath());
			if (!script.exists()) {
				abort(
						NLS
								.bind(
										InterpreterMessages.errDebuggingEngineScriptFileDoesntExist,
										script.toString()), null);
			}
		}
	}
	
	@Override
	protected String[] renderCommandLine(InterpreterConfig config) {
		return config.renderCommandLine(interpreterInstall);
	}
	
	/**
	 * @since 2.0
	 */
	@Override
	protected String[] getEnvironmentVariablesAsStrings(InterpreterConfig config) {
		return config.getEnvironmentAsStringsIncluding(getInstall().getEnvironmentVariables());
	}
	
}