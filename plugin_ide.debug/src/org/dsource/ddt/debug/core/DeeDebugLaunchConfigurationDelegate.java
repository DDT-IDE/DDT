/*******************************************************************************
 * Copyright (c) 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package org.dsource.ddt.debug.core;


import melnorme.lang.ide.debug.core.AbstractLangDebugLaunchConfigurationDelegate;

import org.eclipse.cdt.dsf.gdb.launching.GdbLaunch;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.model.ISourceLocator;

public class DeeDebugLaunchConfigurationDelegate extends AbstractLangDebugLaunchConfigurationDelegate {
	
	@Override
	protected void setAttributes(ILaunchConfiguration configuration, ILaunchConfigurationWorkingCopy workingCopy)
			throws CoreException {
		super.setAttributes(configuration, workingCopy);
		
		// Remove some DLTK attributes that affect how our launch runs
		cleanDLTKDebugConfig(workingCopy);
	}
	
	@Override
	protected GdbLaunch doCreateGdbLaunch(ILaunchConfiguration configuration, String mode, ISourceLocator locator) {
		return new DeeGdbLaunch(configuration, mode, locator);
	}
	
}