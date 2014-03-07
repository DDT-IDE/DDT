package melnorme.lang.ide.core;

import mmrnmhrm.core.DeeCore;

import org.eclipse.core.runtime.Plugin;

public class LangCore_Actual {
	
	public static final String PLUGIN_ID = DeeCore.PLUGIN_ID;
	public static final String NATURE_ID = DeeCore.PLUGIN_ID +".nature";
	
	public static Plugin getInstance() {
		return DeeCore.getInstance();
	}
	
}