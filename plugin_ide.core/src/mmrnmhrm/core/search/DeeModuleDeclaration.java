package mmrnmhrm.core.search;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import org.eclipse.dltk.ast.parser.IModuleDeclaration;

import dtool.ast.definitions.Module;
import dtool.parser.DeeParserResult.ParsedModule;

public class DeeModuleDeclaration implements IModuleDeclaration {

	public final ParsedModule deeParserResult;
	
	public DeeModuleDeclaration(ParsedModule deeParserResult) {
		this.deeParserResult = assertNotNull(deeParserResult);
	}
	
	public Module getModule() {
		return deeParserResult.module;
	}
	
}