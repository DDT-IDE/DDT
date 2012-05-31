package dtool.ast.definitions;

import descent.internal.compiler.parser.InterfaceDeclaration;
import descent.internal.compiler.parser.PROT;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;

/**
 * A definition of an interface aggregate. 
 */
public class DefinitionInterface extends DefinitionClass {

	
	public DefinitionInterface(InterfaceDeclaration elem, ASTConversionContext convContext) {
		super(elem, convContext);
	}
	
	public DefinitionInterface(DefUnitDataTuple dudt, PROT prot, ASTNeoNode[] members, BaseClass[] superClasses) {
		super(dudt, prot, members, superClasses);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		acceptNodeChildren(visitor, children);
		visitor.endVisit(this);
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Interface;
	}

}
