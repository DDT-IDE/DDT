package mmrnmhrm.ui.actions;

import mmrnmhrm.ui.DeeUILanguageToolkit;

import org.eclipse.dltk.ui.IDLTKUILanguageToolkit;
import org.eclipse.dltk.ui.actions.OpenMethodAction;

public class DeeOpenMethodAction extends OpenMethodAction {
	
	@Override
	protected IDLTKUILanguageToolkit getUILanguageToolkit() {
		return DeeUILanguageToolkit.getDefault();
	}
	
	@Override
	protected String getOpenMethodDialogTitle() {
		return "Open D Function";
	}
	
	@Override
	protected String getOpenMethodDialogMessage() {
		return "&Select a function/method to open (? = any character, * = any String, TZ = TimeZone):";
	}
	
	@Override
	protected String getOpenMethodErrorTitle() {
		return getOpenMethodDialogTitle();
	}
	
	@Override
	protected String getOpenMethodErrorMessage() {
		return "An exception occurred while opening the function/method.";
	}
	
	@Override
	public void run() {
		// TODO: need to have extension to customize icons
		super.run();
	}
	
}