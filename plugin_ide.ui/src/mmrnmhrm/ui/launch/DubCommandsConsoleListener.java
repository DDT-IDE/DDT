/*******************************************************************************
 * Copyright (c) 2014, 2014 IBM Corporation and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui.launch;

import melnorme.lang.ide.ui.tools.console.AbstractToolsConsoleHandler;
import mmrnmhrm.ui.DeeUIMessages;

import org.eclipse.core.resources.IProject;

public class DubCommandsConsoleListener extends AbstractToolsConsoleHandler {
	
	public DubCommandsConsoleListener() {
	}
	
	@Override
	protected boolean useGlobalConsole() {
		return false;
	}
	
	@Override
	protected String getProjectConsoleName(IProject project) {
		return DeeUIMessages.DUB_CONSOLE_NAME + getProjectNameSuffix(project);
	}
	
}