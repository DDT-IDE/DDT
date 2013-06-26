package dtool.ast.expressions;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.EArcheType;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.api.IModuleResolver;

/**
 * A lambda expression in the short, simple syntax:
 * <code> Identifier => AssignExpression </code>
 */
public class ExpSimpleLambda extends Expression {
	
	public final SimpleLambdaDefUnit simpleLambdaDefUnit;
	public final Expression bodyExpression;
	
	public ExpSimpleLambda(SimpleLambdaDefUnit simpleLambdaDefUnit, Expression bodyExpression) {
		this.simpleLambdaDefUnit = parentize(simpleLambdaDefUnit);
		this.bodyExpression = parentize(bodyExpression);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.EXP_SIMPLE_LAMBDA;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, simpleLambdaDefUnit);
			TreeVisitor.acceptChildren(visitor, bodyExpression);
		}
		visitor.endVisit(this);	 
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(simpleLambdaDefUnit, " => ");
		cp.append(bodyExpression);
	}
	
	public static class SimpleLambdaDefUnit extends DefUnit {
		
		public SimpleLambdaDefUnit(ProtoDefSymbol defId) {
			super(defId);
		}
		
		@Override
		public ASTNodeTypes getNodeType() {
			return ASTNodeTypes.SIMPLE_LAMBDA_DEFUNIT;
		}
		
		@Override
		public void accept0(IASTVisitor visitor) {
			boolean children = visitor.visit(this);
			if (children) {
				TreeVisitor.acceptChildren(visitor, defname);
			}
			visitor.endVisit(this);
		}
		
		@Override
		public void toStringAsCode(ASTCodePrinter cp) {
			cp.append(defname);
		}
		
		@Override
		public EArcheType getArcheType() {
			return EArcheType.Variable;
		}
		
		@Override
		public IScopeNode getMembersScope(IModuleResolver moduleResolver) {
			return null;
		}
		
	}
	
}