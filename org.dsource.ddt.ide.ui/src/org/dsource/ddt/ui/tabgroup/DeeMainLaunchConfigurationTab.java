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

import melnorme.lang.ide.ui.launch.MainLaunchConfigurationTab;

import org.eclipse.core.resources.IResource;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;


public class DeeMainLaunchConfigurationTab extends MainLaunchConfigurationTab {
	
	public DeeMainLaunchConfigurationTab() {
		super();
	}
	
	@Override
	protected void programPathField_setDefaults(IResource contextualResource, ILaunchConfigurationWorkingCopy config) {
		// TODO: figure out executable path
	}
	
}