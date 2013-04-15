package dtool.ast.expressions;

import static dtool.util.NewUtils.assertNotNull_;
import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.DefUnit.DefUnitTuple;
import dtool.ast.definitions.EArcheType;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.pluginadapters.IModuleResolver;

/**
 * A lambda expression in the short, simple syntax:
 * <code> Identifier => AssignExpression </code>
 */
public class ExpSimpleLambda extends Expression {
	
	public final ExpSimpleLambdaDefUnit simpleLambdaDefUnit;
	public final Expression bodyExpression;
	
	public ExpSimpleLambda(DefUnitTuple defunitData, Expression bodyExpression) {
		this.simpleLambdaDefUnit = parentize(assertNotNull_(new ExpSimpleLambdaDefUnit(defunitData)));
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
		cp.appendNode(simpleLambdaDefUnit, " => ");
		cp.appendNode(bodyExpression); // /*BUG here*/
	}
	
	public static class ExpSimpleLambdaDefUnit extends DefUnit {
		
		public ExpSimpleLambdaDefUnit(DefUnitTuple defunit) {
			super(defunit);
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
			cp.appendNode(defname);
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