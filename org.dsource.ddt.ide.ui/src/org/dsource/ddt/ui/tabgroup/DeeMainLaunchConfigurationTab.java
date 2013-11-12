/*******************************************************************************
 * Copyright (c) 2009, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package org.dsource.ddt.ui.tabgroup;

import org.dsource.ddt.ide.core.DeeNature;
import org.dsource.ddt.lang.ui.tabgroup.MainLaunchConfigurationTab;
import org.eclipse.swt.widgets.Composite;


public class DeeMainLaunchConfigurationTab extends MainLaunchConfigurationTab {
	
	public DeeMainLaunchConfigurationTab() {
		super();
	}
	
	@Override
	public String getNatureID() {
		return DeeNature.NATURE_ID;
	}
	
	@Override
	protected void createMainModuleEditor(Composite parent, String text) {
		super.createMainModuleEditor(parent, DeeLaunchConfigurationsMessages.mainTab_launchFileGroup);
	}
	
}