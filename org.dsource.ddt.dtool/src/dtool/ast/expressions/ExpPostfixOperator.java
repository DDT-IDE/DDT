package dtool.ast.expressions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.parser.DeeTokens;

public class ExpPostfixOperator extends Expression {
	
	public static enum PostfixOpType {
		POST_INCREMENT(DeeTokens.INCREMENT),
		POST_DECREMENT(DeeTokens.DECREMENT),
		;
		
		public final DeeTokens token;
		
		private PostfixOpType(DeeTokens token) {
			this.token = token;
			assertTrue(token.getSourceValue() != null);
		}
		
		public static PostfixOpType tokenToPrefixOpType(DeeTokens token) {
			assertTrue(token == DeeTokens.INCREMENT || token == DeeTokens.DECREMENT);
			return token == DeeTokens.INCREMENT ? POST_INCREMENT : POST_DECREMENT;
		}
	}
	
	public final PostfixOpType kind;
	public final Resolvable exp;
	
	public ExpPostfixOperator(Resolvable exp, PostfixOpType kind) {
		this.exp = parentize(exp);
		this.kind = kind;
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.EXP_POSTFIX_OP;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, exp);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.appendNode(exp);
		cp.append(kind.token.getSourceValue());
	}
	
}