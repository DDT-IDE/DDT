package mmrnmhrm.core.launch;

import org.eclipse.dltk.launching.AbstractInterpreterRunner;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.InterpreterConfig;

public class DeeInterpreterRunner extends AbstractInterpreterRunner {
	
	public DeeInterpreterRunner(IInterpreterInstall install) {
		super(install);
	}
	
	@Override
	protected String getProcessType() {
		return DeeLaunchConfigurationConstants.ID_DEE_PROCESS_TYPE;
	}
	
	
	@Override
	protected String[] renderCommandLine(InterpreterConfig config) {
		return super.renderCommandLine(config);
	}
	
	@Override
	protected String renderCommandLineLabel(InterpreterConfig config) {
		return super.renderCommandLineLabel(config);
	}
}

