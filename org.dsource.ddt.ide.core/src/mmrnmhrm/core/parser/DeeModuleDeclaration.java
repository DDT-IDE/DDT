package mmrnmhrm.core.parser;

import org.eclipse.dltk.ast.parser.IModuleDeclaration;

import dtool.ast.IASTNode;
import dtool.ast.definitions.Module;

public class DeeModuleDeclaration implements IModuleDeclaration {

	public final Module module;
	
	public DeeModuleDeclaration(Module module) {
		this.module = module;
	}
	
	public IASTNode getModuleNode() {
		return module;
	}
	
}