package dtool.ast.definitions;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.DeclList;
import dtool.ast.IASTVisitor;
import dtool.ast.expressions.Expression;
import dtool.ast.references.Reference;
import dtool.util.ArrayView;

/**
 * A definition of an interface aggregate. 
 */
public class DefinitionInterface extends DefinitionClass {
	
	public DefinitionInterface(ProtoDefSymbol defId, ArrayView<TemplateParameter> tplParams,
		Expression tplConstraint, ArrayView<Reference> baseClasses, DeclList decls) {
		super(defId, tplParams, tplConstraint, baseClasses, decls);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DEFINITION_INTERFACE;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		acceptNodeChildren(visitor, children);
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		classLikeToStringAsCode(cp, "interface ");
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Interface;
	}
	
}