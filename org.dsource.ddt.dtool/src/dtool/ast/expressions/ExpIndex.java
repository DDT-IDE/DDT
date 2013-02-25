package dtool.ast.expressions;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.SourceRange;
import dtool.util.ArrayView;

public class ExpIndex extends Expression {
	
	public final Expression indexee;
	public final ArrayView<Expression> args;
	
	public ExpIndex(Expression indexee, ArrayView<Expression> args, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.indexee = parentize(indexee);
		this.args = parentize(args);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.EXP_INDEX;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, indexee);
			TreeVisitor.acceptChildren(visitor, args);
		}
		visitor.endVisit(this);	 
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(indexee);
		cp.appendArgList("[", args, ", " , "]");
	}
	
}