package dtool.ast.expressions;

import java.util.Collection;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.references.Reference;
import dtool.engine.common.IDeeNamedElement;
import dtool.engine.modules.IModuleResolver;

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
	public Collection<IDeeNamedElement> findTargetDefElements(IModuleResolver mr, boolean findFirstOnly) {
		return findTargetElementsForReference(mr, type, findFirstOnly);
	}
	
}