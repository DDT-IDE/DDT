/*******************************************************************************
 * Copyright (c) 2008, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui.preferences;

import mmrnmhrm.core.model.DeeNature;

import org.eclipse.dltk.internal.debug.ui.interpreters.AddScriptInterpreterDialog;
import org.eclipse.dltk.internal.debug.ui.interpreters.InterpretersBlock;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.IInterpreterInstallType;
import org.eclipse.dltk.launching.ScriptRuntime;

public class DeeCompilersBlock extends InterpretersBlock {
	
	@Override
	protected AddScriptInterpreterDialog createInterpreterDialog(IInterpreterInstall standin) {
		IInterpreterInstallType[] deeInstallTypes = ScriptRuntime.getInterpreterInstallTypes(getCurrentNature());
		DialogAddDeeCompiler dialog = new DialogAddDeeCompiler(this, getShell(), deeInstallTypes, standin);
		return dialog;
	}
	
	@Override
	protected String getCurrentNature() {
		return DeeNature.NATURE_ID;
	}
	
}
