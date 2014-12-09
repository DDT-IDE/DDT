package dtool.ast.definitions;

import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.utilbox.collections.ArrayView;
import dtool.ast.expressions.Expression;
import dtool.parser.common.Token;

/**
 * A definition of a struct aggregate.
 */
public class DefinitionStruct extends DefinitionAggregate {
	
	public DefinitionStruct(Token[] comments, ProtoDefSymbol defId, ArrayView<ITemplateParameter> tplParams,
		Expression tplConstraint, IAggregateBody aggrBody) {
		super(comments, defId, tplParams, tplConstraint, aggrBody);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DEFINITION_STRUCT;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptNodeChildren(visitor);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		aggregateToStringAsCode(cp, "struct ", true);
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Struct;
	}
	
}