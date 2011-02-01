package mmrnmhrm.ui.wizards;

import mmrnmhrm.core.model.DeeNature;

import org.eclipse.dltk.internal.debug.ui.interpreters.AbstractInterpreterContainerWizardPage;


public class DeeCompilerContainerWizardPage extends AbstractInterpreterContainerWizardPage {
	
	@Override
	public String getScriptNature() {
		return DeeNature.NATURE_ID; 
	}
	
}
