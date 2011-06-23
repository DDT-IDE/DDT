package dtool.ast.definitions;

import java.util.List;

import descent.internal.compiler.parser.StructDeclaration;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.descentadapter.DefinitionConverter;
import dtool.descentadapter.DescentASTConverter;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;
import dtool.refmodel.IScope;

/**
 * A definition of a struct aggregate.
 */
public class DefinitionStruct extends DefinitionAggregate {
	
	public TemplateParameter[] templateParams; 
	
	
	public DefinitionStruct(StructDeclaration elem, ASTConversionContext convContext) {
		super(DefinitionConverter.convertDsymbol(elem, convContext), elem.prot(),
				DescentASTConverter.convertManyToView(elem.members, ASTNeoNode.class, convContext));
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
		return EArcheType.Struct;
	}
	
	@Override
	public List<IScope> getSuperScopes() {
		return null;
	}

}
