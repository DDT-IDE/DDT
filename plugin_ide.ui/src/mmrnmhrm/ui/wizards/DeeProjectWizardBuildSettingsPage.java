/*******************************************************************************
 * Copyright (c) 2014, 2015 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui.wizards;

import mmrnmhrm.ui.preferences.DubProjectOptionsBlock;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

public class DeeProjectWizardBuildSettingsPage extends WizardPage {
	
	private static final String PAGE_NAME = DeeProjectWizardBuildSettingsPage.class.getSimpleName();
	
	protected final DeeProjectWizard deeNewProjectWizard;
	protected final DubProjectOptionsBlock prjBuildOptionsBlock = new DubProjectOptionsBlock();
	
	public DeeProjectWizardBuildSettingsPage(DeeProjectWizard deeNewProjectWizard) {
		super(PAGE_NAME);
		this.deeNewProjectWizard = deeNewProjectWizard;
		setTitle("Configure DUB build options");
	}
	
	
	@Override
	public void createControl(Composite parent) {
		setControl(prjBuildOptionsBlock.createComponent(parent));
	}
	
	@Override
	public void setVisible(boolean visible) {
		if (visible) {
			prjBuildOptionsBlock.initializeFrom(deeNewProjectWizard.getProject());
		}
		super.setVisible(visible);
	}
	
	public boolean performOk() {
		return prjBuildOptionsBlock.performOk();
	}
	
}