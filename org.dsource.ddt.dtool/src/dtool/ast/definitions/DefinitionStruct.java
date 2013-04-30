package dtool.ast.definitions;

import java.util.List;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.DeclList;
import dtool.ast.IASTVisitor;
import dtool.ast.expressions.Expression;
import dtool.refmodel.IScope;
import dtool.refmodel.pluginadapters.IModuleResolver;
import dtool.util.ArrayView;

/**
 * A definition of a struct aggregate.
 */
public class DefinitionStruct extends DefinitionAggregate {
	
	public DefinitionStruct(ProtoDefSymbol defId, ArrayView<TemplateParameter> tplParams,
		Expression tplConstraint, DeclList decls) {
		super(defId, tplParams, tplConstraint, decls);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DEFINITION_STRUCT;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		acceptNodeChildren(visitor, children);
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		aggregateToStringAsCode(cp, "struct ", true);
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Struct;
	}
	
	@Override
	public List<IScope> getSuperScopes(IModuleResolver moduleResolver) {
		return null;
	}
	
}