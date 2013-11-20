package mmrnmhrm.core.compiler_installs;

import java.util.List;

import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.launching.IInterpreterContainerExtension;
import org.eclipse.dltk.launching.IInterpreterContainerExtension3;
import org.eclipse.dltk.launching.IInterpreterInstall;

public class DeeInterpreterContainerExtension 
	implements IInterpreterContainerExtension, IInterpreterContainerExtension3
{
	
	public DeeInterpreterContainerExtension() {
	}
	
	@Override
	public void processEntres(IScriptProject project, List<IBuildpathEntry> entries) {
		// Nothing required to do
	}
	
	@Override
	public String getDescription(IInterpreterInstall interpreterInstall) {
		return "D Standard Library";
	}
	
}