package mmrnmhrm.ui.preferences.pages;

import mmrnmhrm.ui.preferences.DeeProjectOptionsBlock;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.PropertyPage;


public class DeeProjectOptionsPropertyPage extends PropertyPage {
	
	private DeeProjectOptionsBlock fProjCfg;

	public DeeProjectOptionsPropertyPage() {
		fProjCfg = new DeeProjectOptionsBlock();
	}
	
	@Override
	protected Control createContents(Composite parent) {
		
		noDefaultAndApplyButton();		
		
		if (getProject() == null) {
			Label label = new Label(parent, SWT.NONE);
			label.setText("Target not a D project.");
			setVisible(false);
			return label;
		} else {
			fProjCfg.init2(DLTKCore.create(getProject()));
			return fProjCfg.createControl(parent);
		}
	}
	
	private IProject getProject() {
		IAdaptable adaptable= getElement();
		if(adaptable instanceof IProject) {
			return (IProject) adaptable;
		}
		return (IProject) adaptable.getAdapter(IProject.class);
	}

	@Override
	public boolean performOk() {
		return fProjCfg.performOk();
	}
	
}