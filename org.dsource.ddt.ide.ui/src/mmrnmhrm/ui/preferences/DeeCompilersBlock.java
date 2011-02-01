package mmrnmhrm.ui.preferences;

import mmrnmhrm.core.model.DeeNature;

import org.eclipse.dltk.internal.debug.ui.interpreters.AddScriptInterpreterDialog;
import org.eclipse.dltk.internal.debug.ui.interpreters.InterpretersBlock;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.ScriptRuntime;

public class DeeCompilersBlock extends InterpretersBlock {

	@Override
	protected AddScriptInterpreterDialog createInterpreterDialog(IInterpreterInstall standin) {
		DialogAddDeeCompiler dialog = new DialogAddDeeCompiler(this, getShell(), ScriptRuntime
				.getInterpreterInstallTypes(getCurrentNature()), standin);
		return dialog;
	}

	@Override
	protected String getCurrentNature() {
		return DeeNature.NATURE_ID;
	}
	
}
 