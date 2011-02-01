package mmrnmhrm.ui.wizards;

import mmrnmhrm.ui.preferences.pages.DeeCompilersPreferencePage;

import org.eclipse.dltk.ui.wizards.ProjectWizardFirstPage;
import org.eclipse.swt.widgets.Composite;

public class DeeProjectWizardPage1 extends ProjectWizardFirstPage {
	
	final class DeeInterpreterGroup extends AbstractInterpreterGroup {
		
		public DeeInterpreterGroup(Composite composite) {
			super (composite);
		}
		
		// Commented since DLTK 2.0
//		@Override
//		protected String getCurrentLanguageNature() {
//			return DeeNature.NATURE_ID;
//		}
		
		@Override
		protected String getIntereprtersPreferencePageId() {
			return DeeCompilersPreferencePage.PAGE_ID;
		}
		
	};
	
	@Override
	protected IInterpreterGroup createInterpreterGroup(Composite parent) {
		return new DeeInterpreterGroup(parent);
	}
	
	@Override
	protected boolean interpeterRequired() {
		return false;
	}
	
	@Override
	public boolean isSrc() {
		return true; // Create src+bin buildpath
	}
}