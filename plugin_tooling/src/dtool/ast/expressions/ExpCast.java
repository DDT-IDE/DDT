package dtool.ast.expressions;

import java.util.Collection;

import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.bundles.ISemanticContext;
import melnorme.lang.tooling.symbols.INamedElement;
import dtool.ast.references.Reference;

public class ExpCast extends Expression {
	
	public final Reference type;
	public final Resolvable exp;
	
	public ExpCast(Reference castType, Expression exp) {
		this.exp = parentize(exp);
		this.type = parentize(castType);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.EXP_CAST;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, type);
		acceptVisitor(visitor, exp);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("cast");
		cp.append("(", type, ")");
		cp.append(exp);
	}
	
	@Override
	public Collection<INamedElement> findTargetDefElements(ISemanticContext mr, boolean findFirstOnly) {
		return findTargetElementsForReference(mr, type, findFirstOnly);
	}
	
}