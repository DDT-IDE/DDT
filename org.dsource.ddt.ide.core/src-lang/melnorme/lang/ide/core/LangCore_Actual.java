package melnorme.lang.ide.core;

import org.eclipse.core.runtime.Plugin;

import mmrnmhrm.core.DeeCore;

public class LangCore_Actual {
	
	public static String PLUGIN_ID = DeeCore.PLUGIN_ID;
	
	public static Plugin getInstance() {
		return DeeCore.getInstance();
	}
	
}