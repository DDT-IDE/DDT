package org.dsource.ddt.ide.core;

import melnorme.lang.ide.core.LangNature;
import mmrnmhrm.core.build.DubProjectBuilder;

public class DeeNature extends LangNature {
	
	@Override
	protected String getBuilderId() {
		return DubProjectBuilder.BUILDER_ID;
	}
	
}