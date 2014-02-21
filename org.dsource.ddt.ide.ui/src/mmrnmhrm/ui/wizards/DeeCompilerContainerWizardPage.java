package mmrnmhrm.ui.wizards;


import org.dsource.ddt.ide.core.DeeNature;
import org.eclipse.dltk.internal.debug.ui.interpreters.AbstractInterpreterContainerWizardPage;

@Deprecated
public class DeeCompilerContainerWizardPage extends AbstractInterpreterContainerWizardPage {
	
	@Override
	public String getScriptNature() {
		return DeeNature.NATURE_ID; 
	}
	
}
