package dtool.ast.definitions;

import java.util.List;

import descent.internal.compiler.parser.PROT;
import descent.internal.compiler.parser.UnionDeclaration;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.descentadapter.DefinitionConverter;
import dtool.descentadapter.DescentASTConverter;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.refmodel.IScope;

/**
 * A definition of a struct aggregate.
 */
public class DefinitionUnion extends DefinitionAggregate {
	
	public TemplateParameter[] templateParams; 
	
	
	public DefinitionUnion(UnionDeclaration elem, ASTConversionContext convContext) {
		super(
			DefinitionConverter.convertDsymbol(elem, convContext),
			elem.prot(),
			DescentASTConverter.convertManyToView(elem.members, ASTNeoNode.class, convContext).getInternalArray()
		);
		
		// TODO: where did template Parameters go
		//if(elem.templateParameters != null)
		//	this.templateParams = TemplateParameter.convertMany(elem.templateParameters);
	}
	
	public DefinitionUnion(DefUnitDataTuple dudt, PROT prot, ASTNeoNode[] members) {
		super(dudt, prot, members);
	}
	
	@Override	
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		acceptNodeChildren(visitor, children);
		visitor.endVisit(this);
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Union;
	}
	
	@Override
	public List<IScope> getSuperScopes() {
		return null;
	}
}
