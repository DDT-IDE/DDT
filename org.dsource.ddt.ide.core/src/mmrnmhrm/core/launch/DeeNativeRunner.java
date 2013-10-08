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

import melnorme.lang.launching.AbstractNativeExecutableRunner;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.launching.InterpreterConfig;

public class DeeNativeRunner extends AbstractNativeExecutableRunner {
	
	@Override
	protected String getProcessType() {
		return DeeLaunchConfigurationConstants.ID_DEE_PROCESS_TYPE;
	}
	
	@Override
	protected void checkConfig(InterpreterConfig config) throws CoreException {
		super.checkConfig(config);
	}
	
}