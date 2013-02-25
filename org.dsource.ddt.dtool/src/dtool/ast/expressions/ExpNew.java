package dtool.ast.expressions;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.SourceRange;
import dtool.ast.references.RefIndexing;
import dtool.ast.references.Reference;
import dtool.util.ArrayView;

/**
 * New expression.
 * Note that the:
 * <code> new AllocatorArgumentsopt Type [ AssignExpression ] </code>
 * case is parsed as a {@link RefIndexing} containing Type and AssignExpression. 
 * Semantic analysis would be necessary to disambiguate.
 */
public class ExpNew extends Expression {
	
	public final ArrayView<Expression> allocArgs;
	public final Reference newtype;
	public final ArrayView<Expression> args;
	
	public ExpNew(ArrayView<Expression> atorArgs, Reference type, ArrayView<Expression> args, 
		SourceRange sourceRange) {
		this.allocArgs = parentize(atorArgs);
		this.newtype = parentize(type);
		this.args = parentize(args);
		initSourceRange(sourceRange);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.EXP_NEW;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, allocArgs);
			TreeVisitor.acceptChildren(visitor, newtype);
			TreeVisitor.acceptChildren(visitor, args);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("new");
		cp.appendArgList("(", allocArgs, ", ", ")", " "); 
		cp.appendNode(newtype);
		cp.appendArgList("(", args, ", ", ")", " ");
	}
	
}