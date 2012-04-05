package dtool.ast.definitions;

import java.util.List;

import descent.internal.compiler.parser.PROT;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.refmodel.IScope;

/**
 * A definition of a struct aggregate.
 */
public class DefinitionUnion extends DefinitionAggregate {
	
	public DefinitionUnion(DefUnitDataTuple dudt, PROT prot, ASTNeoNode[] members) {
		super(dudt, prot, members);
		// TODO: where did template Parameters go
		//if(elem.templateParameters != null)
		//	this.templateParams = TemplateParameter.convertMany(elem.templateParameters);
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
