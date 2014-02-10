package melnorme.lang.ide.core;

import org.dsource.ddt.ide.core.DeeNature;
import org.eclipse.core.runtime.Plugin;

import mmrnmhrm.core.DeeCore;

public class LangCore_Actual {
	
	public static final String PLUGIN_ID = DeeCore.PLUGIN_ID;
	public static final String NATURE_ID = DeeNature.NATURE_ID;
	
	public static Plugin getInstance() {
		return DeeCore.getInstance();
	}
	
}