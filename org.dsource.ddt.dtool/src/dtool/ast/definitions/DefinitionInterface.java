package dtool.ast.definitions;

import descent.internal.compiler.parser.InterfaceDeclaration;
import dtool.ast.IASTNeoVisitor;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;

/**
 * A definition of an interface aggregate. 
 */
public class DefinitionInterface extends DefinitionClass {

	
	public DefinitionInterface(InterfaceDeclaration elem, ASTConversionContext convContext) {
		super(elem, convContext);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		acceptNodeChildren(visitor, children);
		visitor.endVisit(this);
	}

}
