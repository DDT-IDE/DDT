package dtool.ast.definitions;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.expressions.Expression;
import dtool.parser.Token;
import dtool.util.ArrayView;

/**
 * A definition of an union aggregate.
 */
public class DefinitionUnion extends DefinitionAggregate {
	
	public DefinitionUnion(Token[] comments, ProtoDefSymbol defId, ArrayView<TemplateParameter> tplParams,
		Expression tplConstraint, IAggregateBody aggrBody) {
		super(comments, defId, tplParams, tplConstraint, aggrBody);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DEFINITION_UNION;
	}
	
	@Override	
	public void visitChildren(IASTVisitor visitor) {
		acceptNodeChildren(visitor);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		aggregateToStringAsCode(cp, "union ", true);
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Union;
	}
	
}