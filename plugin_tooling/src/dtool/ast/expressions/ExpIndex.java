package dtool.ast.expressions;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.NodeListView;

public class ExpIndex extends Expression {
	
	public final Expression indexee;
	public final NodeListView<Expression> args;
	
	public ExpIndex(Expression indexee, NodeListView<Expression> args) {
		this.indexee = parentize(indexee);
		this.args = parentize(args);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.EXP_INDEX;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, indexee);
		acceptVisitor(visitor, args);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(indexee);
		cp.appendNodeList("[", args, ", " , "]");
	}
	
}