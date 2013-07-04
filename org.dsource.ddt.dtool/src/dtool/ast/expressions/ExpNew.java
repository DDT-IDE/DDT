package dtool.ast.expressions;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.NodeListView;
import dtool.ast.references.RefIndexing;
import dtool.ast.references.Reference;

/**
 * New expression.
 * Note that the:
 * <code> new AllocatorArgumentsopt Type [ AssignExpression ] </code>
 * case is parsed as a {@link RefIndexing} containing Type and AssignExpression. 
 * Semantic analysis would be necessary to disambiguate.
 */
public class ExpNew extends Expression {
	
	public final NodeListView<Expression> allocArgs;
	public final Reference newtype;
	public final NodeListView<Expression> args;
	
	public ExpNew(NodeListView<Expression> atorArgs, Reference type, NodeListView<Expression> args) {
		this.allocArgs = parentize(atorArgs);
		this.newtype = parentize(type);
		this.args = parentize(args);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.EXP_NEW;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, allocArgs);
		acceptVisitor(visitor, newtype);
		acceptVisitor(visitor, args);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("new");
		cp.appendNodeList("(", allocArgs, ", ", ")", " "); 
		cp.append(newtype);
		cp.appendNodeList("(", args, ", ", ")", " ");
	}
	
}