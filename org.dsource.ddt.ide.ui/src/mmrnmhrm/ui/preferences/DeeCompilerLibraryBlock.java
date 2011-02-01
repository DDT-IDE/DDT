package mmrnmhrm.ui.preferences;


import mmrnmhrm.ui.DeePlugin;

import org.eclipse.dltk.internal.debug.ui.interpreters.AbstractInterpreterLibraryBlock;
import org.eclipse.dltk.internal.debug.ui.interpreters.AddScriptInterpreterDialog;
import org.eclipse.dltk.internal.debug.ui.interpreters.LibraryLabelProvider;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IBaseLabelProvider;

/**
 * Control used to edit the libraries associated with a Interpreter install
 */
public class DeeCompilerLibraryBlock extends AbstractInterpreterLibraryBlock {

	/** the prefix for dialog setting pertaining to this block */
	protected static final String DIALOG_SETTINGS_PREFIX = "DeeCompilerLibraryBlock"; //$NON-NLS-1$

	public DeeCompilerLibraryBlock(AddScriptInterpreterDialog dialog) {
	    super(dialog);
	}
	
	@Override
	protected IBaseLabelProvider getLabelProvider() {
		return new LibraryLabelProvider();
	}
	
	@Override
	protected IDialogSettings getDialogSettions() {
		return DeePlugin.getDefault().getDialogSettings();
		//return RubyDebugUIPlugin.getDefault().getDialogSettings();
	}
}
