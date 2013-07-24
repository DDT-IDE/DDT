package mmrnmhrm.core.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import org.eclipse.dltk.ast.parser.IModuleDeclaration;

import dtool.ast.IASTNode;
import dtool.ast.definitions.Module;
import dtool.parser.DeeParserResult;

public class DeeModuleDeclaration implements IModuleDeclaration {

	public final DeeParserResult deeParserResult;
	
	public DeeModuleDeclaration(DeeParserResult deeParserResult) {
		this.deeParserResult = assertNotNull(deeParserResult);
	}
	
	public IASTNode getModuleNode() {
		return deeParserResult.module;
	}
	
	public Module getModule() {
		return deeParserResult.module;
	}
	
}