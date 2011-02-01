package mmrnmhrm.ui.wizards;

import mmrnmhrm.ui.preferences.DeeBuildpathsBlock;

import org.eclipse.dltk.ui.util.BusyIndicatorRunnableContext;
import org.eclipse.dltk.ui.util.IStatusChangeListener;
import org.eclipse.dltk.ui.wizards.BuildpathsBlock;
import org.eclipse.dltk.ui.wizards.ProjectWizardFirstPage;
import org.eclipse.dltk.ui.wizards.ProjectWizardSecondPage;

public class DeeProjectWizardPage2 extends ProjectWizardSecondPage {
	
	public DeeProjectWizardPage2(ProjectWizardFirstPage mainPage) {
		super(mainPage);
	}
	
	@Override
	protected BuildpathsBlock createBuildpathBlock(IStatusChangeListener listener) {
		return new DeeBuildpathsBlock(new BusyIndicatorRunnableContext(), listener, 0, useNewSourcePage(), null);
	}
	
}