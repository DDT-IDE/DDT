package dtool.resolver;

import melnorme.utilbox.misc.StringUtil;
import dtool.ast.definitions.Module;
import dtool.engine.modules.IModuleResolver;
import dtool.util.NewUtils;

public class ResolverUtil {
	
	public static Module findModule_unchecked(IModuleResolver mr, String fullyQualifiedName) {
		ModuleNameDescriptor nameDesc = getNameDescriptor(fullyQualifiedName);
		return ReferenceResolver.findModuleUnchecked(mr, nameDesc.packages, nameDesc.moduleName);
	}
	
	public static ModuleNameDescriptor getNameDescriptor(String moduleFullyQualifiedName) {
		String moduleName = StringUtil.substringAfterLastMatch(moduleFullyQualifiedName, ".");
		String packagesSeg = StringUtil.segmentUntilLastMatch(moduleFullyQualifiedName, ".");
		String[] packages = packagesSeg == null ? NewUtils.EMPTY_STRING_ARRAY : packagesSeg.split("\\.");
		return new ModuleNameDescriptor(packages, moduleName);
	}
	
	public static class ModuleNameDescriptor {
		
		public final String[] packages;
		public final String moduleName;
		
		public ModuleNameDescriptor(String[] packages, String moduleName) {
			this.packages = packages;
			this.moduleName = moduleName;
		}
		
	}
	
}