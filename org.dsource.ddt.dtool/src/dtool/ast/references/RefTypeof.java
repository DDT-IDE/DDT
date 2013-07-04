package dtool.ast.references;

import java.util.Collection;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.definitions.DefUnit;
import dtool.ast.expressions.Expression;
import dtool.ast.expressions.Resolvable.IQualifierNode;
import dtool.resolver.api.DefUnitDescriptor;
import dtool.resolver.api.IModuleResolver;

public class RefTypeof extends Reference implements IQualifierNode {
	
	public final Expression expression;
	
	public RefTypeof(Expression exp) {
		this.expression = parentize(exp);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.REF_TYPEOF;
	}
	
	public static class ExpRefReturn extends Expression {
		
		public ExpRefReturn() {}
		
		@Override
		public ASTNodeTypes getNodeType() {
			return ASTNodeTypes.EXP_REF_RETURN;
		}
		
		@Override
		public void visitChildren(IASTVisitor visitor) {
		}
		
		@Override
		public void toStringAsCode(ASTCodePrinter cp) {
			cp.append("return");
		}
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, expression);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("typeof");
		cp.append("(", expression, ")");
	}
	
	@Override
	public Collection<DefUnit> findTargetDefUnits(IModuleResolver moduleResolver, boolean findFirstOnly) {
		return expression.getType(moduleResolver);
	}
	
	@Override
	public boolean canMatch(DefUnitDescriptor defunit) {
		return false;
	}
	
}