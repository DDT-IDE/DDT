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
package mmrnmhrm.ui.launch;

import mmrnmhrm.ui.DeePlugin;

import org.dsource.ddt.ide.core.DeeNature;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.dltk.core.PreferencesLookupDelegate;
import org.eclipse.dltk.debug.core.DLTKDebugPreferenceConstants;
import org.eclipse.dltk.debug.ui.launchConfigurations.MainLaunchConfigurationTab;
import org.eclipse.swt.widgets.Composite;


public class DeeMainLaunchConfigurationTab extends MainLaunchConfigurationTab {
	
	public DeeMainLaunchConfigurationTab(String mode) {
		super(mode);
	}
	
	@Override
	public String getNatureID() {
		return DeeNature.NATURE_ID;
	}
	
	@Override
	protected boolean breakOnFirstLinePrefEnabled(PreferencesLookupDelegate delegate) {
		return delegate.getBoolean(DeePlugin.PLUGIN_ID,
				DLTKDebugPreferenceConstants.PREF_DBGP_BREAK_ON_FIRST_LINE);
	}
	
	@Override
	protected boolean dbpgLoggingPrefEnabled(PreferencesLookupDelegate delegate) {
		return delegate.getBoolean(DeePlugin.PLUGIN_ID,
				DLTKDebugPreferenceConstants.PREF_DBGP_ENABLE_LOGGING);
	}
	
	
	@Override
	protected void createMainModuleEditor(Composite parent, String text) {
		super.createMainModuleEditor(parent, DeeLaunchConfigurationsMessages.mainTab_launchFileGroup);
	}
	
	// Don't do any custom GUI controls for now
	@Override
	protected void updateProjectFromConfig(ILaunchConfiguration config) {
		super.updateProjectFromConfig(config);
	}
	
	@Override
	protected void doInitializeForm(ILaunchConfiguration config) {
		super.doInitializeForm(config);
	}
	
	@Override
	protected void updateMainModuleFromConfig(ILaunchConfiguration config) {
		super.updateMainModuleFromConfig(config);
	}
	
}
