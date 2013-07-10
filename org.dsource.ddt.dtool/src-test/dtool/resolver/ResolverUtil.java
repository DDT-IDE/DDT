package dtool.resolver;

import melnorme.utilbox.misc.StringUtil;
import dtool.ast.definitions.Module;
import dtool.resolver.ResolverSourceTests.ITestsModuleResolver;
import dtool.util.NewUtils;

public class ResolverUtil {
	
	public static Module findModule_unchecked(ITestsModuleResolver mr, String fullyQualifiedName) {
		try {
			return findModule(mr, fullyQualifiedName);
		} catch(Exception e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
	}
	
	public static Module findModule(ITestsModuleResolver mr, String fullyQualifiedName) throws Exception {
		String module = StringUtil.substringAfterMatch(fullyQualifiedName, ".");
		String packagesSeg = StringUtil.segmentUntilMatch(fullyQualifiedName, ".");
		String[] packages = packagesSeg == null ? NewUtils.EMPTY_STRING_ARRAY : packagesSeg.split("\\.");
		return mr.findModule(packages, module);
	}
	
}
