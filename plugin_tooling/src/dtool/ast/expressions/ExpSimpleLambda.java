package dtool.ast.expressions;

import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.engine.INamedElementSemantics;
import melnorme.lang.tooling.engine.resolver.NullElementSemantics;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.EArcheType;

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
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, simpleLambdaDefUnit);
		acceptVisitor(visitor, bodyExpression);
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
		public void visitChildren(IASTVisitor visitor) {
			acceptVisitor(visitor, defname);
		}
		
		@Override
		public void toStringAsCode(ASTCodePrinter cp) {
			cp.append(defname);
		}
		
		@Override
		public EArcheType getArcheType() {
			return EArcheType.Variable;
		}
		
		/* -----------------  ----------------- */
		
		@Override
		public INamedElementSemantics getNodeSemantics() {
			return semantics;
		}
		
		protected final INamedElementSemantics semantics = new NullElementSemantics(); // TODO
		
	}
	
}