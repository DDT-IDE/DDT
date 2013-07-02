package dtool.ast.expressions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;

public class ExpParentheses extends Expression {
	
	public final boolean isDotAfterParensSyntax;
	public final Resolvable resolvable;
	
	public ExpParentheses(boolean isDotAfterParensSyntax, Resolvable resolvable) {
		this.isDotAfterParensSyntax = isDotAfterParensSyntax;
		this.resolvable = parentize(assertNotNull(resolvable));
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.EXP_PARENTHESES;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, resolvable);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("(", resolvable, ")");
	}
}