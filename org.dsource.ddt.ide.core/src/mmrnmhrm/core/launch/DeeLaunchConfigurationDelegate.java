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
package mmrnmhrm.core.launch;


import org.dsource.ddt.ide.core.DeeNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.dltk.launching.InterpreterConfig;

public class DeeLaunchConfigurationDelegate extends AbstractScriptLaunchConfigurationDelegateExtension {
	
	@Override
	public String getLanguageId() {
		return DeeNature.NATURE_ID;
	}
	
	@Override
	protected void launch0(InterpreterConfig config, ILaunchConfiguration configuration, ILaunch launch,
			IProgressMonitor monitor) throws CoreException {
		new DeeNativeRunner().run(config, launch, monitor);
	}
	
}