package mmrnmhrm.core.launch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import mmrnmhrm.core.model.DeeNature;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.environment.EnvironmentManager;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IExecutionEnvironment;
import org.eclipse.dltk.launching.AbstractScriptLaunchConfigurationDelegate;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.InterpreterConfig;

public class DeeLaunchConfigurationDelegate extends AbstractScriptLaunchConfigurationDelegate {

	/** This specialized InterpreterConfig ignores the interpreter (which in this case
	 * is the DMD compiler) and runs the executable directly. */
	public static class DeeInterpreterConfig extends InterpreterConfig {
		
		public DeeInterpreterConfig(IEnvironment scriptEnvironment, IPath mainScript, IPath workingDirectory) {
			super(scriptEnvironment, mainScript, workingDirectory);
		}
		
		@Override
		public String[] renderCommandLine(IInterpreterInstall interpreter) {
			return renderCommandLine();
		}
		
		@Override
		public String[] renderCommandLine(IEnvironment environment, String interpreter) {
			return renderCommandLine(null);
		}
		
		@Override
		protected String[] renderCommandLine(IEnvironment environment, IPath interpreter) {
			return renderCommandLine(null);
		}

		@SuppressWarnings("unchecked")
		private String[] renderCommandLine() {
			List<String> items = new ArrayList<String>();

			items.add(getScriptFilePath().toString());

			// Script arguments
			List<String> scriptArgs = (List<String>) getScriptArgs();
			items.addAll(scriptArgs);

			return (String[]) items.toArray(new String[items.size()]);
		}
	}
	
	@Override
	public String getLanguageId() {
		return DeeNature.NATURE_ID;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	// Note: this is code copied from DLTK super.createInterpreterConfig
	protected InterpreterConfig createInterpreterConfig(ILaunchConfiguration configuration,
			ILaunch launch) throws CoreException {

		// Validation already included
		IEnvironment scriptEnvironment = getScriptEnvironment(configuration);
		IExecutionEnvironment scriptExecEnvironment = (IExecutionEnvironment) scriptEnvironment
				.getAdapter(IExecutionEnvironment.class);
		String scriptLaunchPath = getScriptLaunchPath(configuration, scriptEnvironment);
		// if (scriptLaunchPath == null) {
		// return null;
		// }
		final IPath workingDirectory = new Path(getWorkingDirectory(configuration,
				scriptEnvironment));

		IPath mainScript = null;//
		if (scriptLaunchPath != null) {
			mainScript = new Path(scriptLaunchPath);
		}
		InterpreterConfig config = new DeeInterpreterConfig(scriptEnvironment, mainScript,
				workingDirectory);

		// Script arguments
		String[] scriptArgs = getScriptArguments(configuration);
		config.addScriptArgs(scriptArgs);

		// Interpreter argument
		String[] interpreterArgs = getInterpreterArguments(configuration);
		config.addInterpreterArgs(interpreterArgs);

		// Environment
		// config.addEnvVars(DebugPlugin.getDefault().getLaunchManager()
		// .getNativeEnvironmentCasePreserved());
		Map configEnv = configuration.getAttribute(ILaunchManager.ATTR_ENVIRONMENT_VARIABLES,
				new HashMap());
		// build base environment
		Map env = scriptExecEnvironment.getEnvironmentVariables(false);
		boolean append = configuration.getAttribute(
				ILaunchManager.ATTR_APPEND_ENVIRONMENT_VARIABLES, true);
		if (configEnv != null) {
			for (Iterator iterator = configEnv.keySet().iterator(); iterator.hasNext();) {
				String name = (String) iterator.next();
				if (!env.containsKey(name) || !append) {
					env.put(name, configEnv.get(name));
				}
			}
		}
		config.addEnvVars(env);

		return config;
	}

	protected IEnvironment getScriptEnvironment(ILaunchConfiguration configuration)
			throws CoreException {
		IScriptProject scriptProject = AbstractScriptLaunchConfigurationDelegate
				.getScriptProject(configuration);
		return EnvironmentManager.getEnvironment(scriptProject);
	}

}
