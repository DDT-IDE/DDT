package org.dsource.ddt.ide.core;

import melnorme.lang.ide.core.LangCore_Actual;
import melnorme.lang.ide.core.LangNature;

public class DeeNature extends LangNature {
	
	@Override
	protected String getBuilderId() {
		return LangCore_Actual.BUILDER_ID;
	}
	
}