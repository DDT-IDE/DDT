package dtool.resolver.api;

import dtool.ast.definitions.Module;
import dtool.util.NewUtils;

public class NullModuleResolver implements IModuleResolver {
	
	@Override
	public String[] findModules(String fqNamePrefix) throws Exception {
		return NewUtils.EMPTY_STRING_ARRAY;
	}
	
	@Override
	public Module findModule(String[] packages, String module) throws Exception {
		return null;
	}
	
}