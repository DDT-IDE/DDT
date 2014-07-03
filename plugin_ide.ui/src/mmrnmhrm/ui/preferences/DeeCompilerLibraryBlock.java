/*******************************************************************************
 * Copyright (c) 2014, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui.preferences;


import mmrnmhrm.dltk.ui.interpreters.AbstractInterpreterLibraryBlock;
import mmrnmhrm.dltk.ui.interpreters.AddScriptInterpreterDialog;
import mmrnmhrm.ui.DeeUIPlugin;

import org.eclipse.dltk.internal.debug.ui.interpreters.LibraryLabelProvider;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IBaseLabelProvider;

/**
 * Control used to edit the libraries associated with a Interpreter install
 */
public class DeeCompilerLibraryBlock extends AbstractInterpreterLibraryBlock {

	/** the prefix for dialog setting pertaining to this block */
	protected static final String DIALOG_SETTINGS_PREFIX = "DeeCompilerLibraryBlock";

	public DeeCompilerLibraryBlock(AddScriptInterpreterDialog dialog) {
	    super(dialog);
	}
	
	@Override
	protected IBaseLabelProvider getLabelProvider() {
		return new LibraryLabelProvider();
	}
	
	@Override
	protected IDialogSettings getDialogSettions() {
		return DeeUIPlugin.getInstance().getDialogSettings();
	}
}