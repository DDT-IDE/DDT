/*******************************************************************************
 * Copyright (c) 2014, 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui.preferences.pages;

import mmrnmhrm.ui.preferences.DubProjectOptionsBlock;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.PropertyPage;


public class DubOptionsPropertyPage extends PropertyPage {
	
	protected final DubProjectOptionsBlock prjBuildOptionsBlock = new DubProjectOptionsBlock();
	
	@Override
	protected Control createContents(Composite parent) {
		
		noDefaultAndApplyButton();		
		
		if (getProject() == null) {
			setVisible(false);
			Label label = new Label(parent, SWT.NONE);
			label.setText("Target not a D project.");
			return label;
		} else {
			prjBuildOptionsBlock.initializeFrom(DLTKCore.create(getProject()));
			return prjBuildOptionsBlock.createControl(parent);
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
		return prjBuildOptionsBlock.performOk();
	}
	
}