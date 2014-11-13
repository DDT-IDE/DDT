package dtool.engine.modules;

import java.util.HashSet;

import dtool.ast.definitions.Module;

public class NullModuleResolver extends CommonModuleResolver {
	
	@Override
	protected HashSet<String> findModules_do(String fqNamePrefix) {
		return new HashSet<>();
	}
	
	@Override
	protected Module findModule_do(String[] packages, String module) {
		return null;
	}
	
}