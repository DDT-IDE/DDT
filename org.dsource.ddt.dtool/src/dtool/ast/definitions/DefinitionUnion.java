package dtool.ast.definitions;

import java.util.List;

import descent.internal.compiler.parser.UnionDeclaration;
import dtool.ast.IASTNeoVisitor;
import dtool.descentadapter.DescentASTConverter;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.refmodel.IScope;

/**
 * A definition of a struct aggregate.
 */
public class DefinitionUnion extends DefinitionAggregate {

	public TemplateParameter[] templateParams; 
	
	
	public DefinitionUnion(UnionDeclaration elem, ASTConversionContext convContext) {
		super(elem, convContext);
		if(elem.members != null)
			this.members = DescentASTConverter.convertManyL(elem.members, this.members, convContext);
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
	public List<IScope> getSuperScopes() {
		return null;
	}
}
