package mmrnmhrm.ui.wizards;

import mmrnmhrm.ui.preferences.pages.DeeCompilersPreferencePage;

import org.eclipse.dltk.ui.wizards.ProjectWizardFirstPage;
import org.eclipse.swt.widgets.Composite;

public class DeeProjectWizardPage1 extends ProjectWizardFirstPage {
	
	protected final DeeProjectWizard deeNewProjectWizard;

	public DeeProjectWizardPage1(DeeProjectWizard deeNewProjectWizard) {
		super();
		this.deeNewProjectWizard = deeNewProjectWizard;
	}
	
	@Override
	protected IInterpreterGroup createInterpreterGroup(Composite parent) {
		return new DeeInterpreterGroup(parent);
	}
	
	final class DeeInterpreterGroup extends AbstractInterpreterGroup {
		
		public DeeInterpreterGroup(Composite composite) {
			super (composite);
		}
		
		@Override
		protected String getIntereprtersPreferencePageId() {
			return DeeCompilersPreferencePage.PAGE_ID;
		}
		
	};
	
	@Override
	protected boolean interpeterRequired() {
		return false;
	}
	
	@Override
	public boolean isSrc() {
		return true; // Create src+bin buildpath
	}
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if(visible) {
			deeNewProjectWizard.pageChanged(this);
		}
	}
	
}