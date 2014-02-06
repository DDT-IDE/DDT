package dtool.project;

import dtool.ast.definitions.Module;
import dtool.util.NewUtils;

public class NullModuleResolver extends CommonModuleResolver {
	
	@Override
	protected String[] findModules_do(String fqNamePrefix) throws Exception {
		return NewUtils.EMPTY_STRING_ARRAY;
	}
	
	@Override
	protected Module findModule_do(String[] packages, String module) throws Exception {
		return null;
	}
	
}