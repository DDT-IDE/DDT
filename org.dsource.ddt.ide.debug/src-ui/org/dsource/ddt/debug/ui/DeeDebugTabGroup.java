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
package org.dsource.ddt.debug.ui;

import org.dsource.ddt.ui.tabgroup.DeeMainLaunchConfigurationTab;
import org.dsource.ddt.ui.tabgroup.DeeScriptArgumentsTab;
import org.eclipse.cdt.launch.ui.CommonTabLite;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.EnvironmentTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.sourcelookup.SourceLookupTab;
import org.eclipse.dltk.debug.ui.launchConfigurations.ScriptCommonTab;

public class DeeDebugTabGroup extends AbstractLaunchConfigurationTabGroup {
	
	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
				new DeeMainLaunchConfigurationTab(mode),
				new DeeScriptArgumentsTab(),
				new EnvironmentTab(),
				new SourceLookupTab(),
				new ScriptCommonTab(), // TODO: use CommonTab or CommonTabLite ?
				new CommonTab(),
				new CommonTabLite()
		};
		
		setTabs(tabs);
	}
	
}
