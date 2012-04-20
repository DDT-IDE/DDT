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

import org.eclipse.dltk.debug.ui.launchConfigurations.ScriptArgumentsTab;
import org.eclipse.dltk.internal.debug.ui.launcher.InterpreterArgumentsBlock;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

final class DeeScriptArgumentsTab extends ScriptArgumentsTab {
	
	@Override
	protected InterpreterArgumentsBlock createInterpreterArgsBlock() {
		return null;
	}
	
	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		
		Composite argumentsParent = fPrgmArgumentsText.getParent();
		if(argumentsParent instanceof Group) {
			Group group = (Group) argumentsParent;
			group.setText(DeeLaunchConfigurationsMessages.argumentsTab_launchArgumentsGroup);
		}
	}
	
}