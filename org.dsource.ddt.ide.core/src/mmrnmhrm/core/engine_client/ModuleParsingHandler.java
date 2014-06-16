package mmrnmhrm.core.engine_client;

import org.eclipse.dltk.core.ISourceModule;

import dtool.parser.DeeParserResult.ParsedModule;

@Deprecated
public class ModuleParsingHandler {
	
	public static ParsedModule parseModule(ISourceModule moduleUnit) {
		return DToolClient.getDefault().getParsedModule_forDeprecatedAPIs(moduleUnit);
	}
	
}
