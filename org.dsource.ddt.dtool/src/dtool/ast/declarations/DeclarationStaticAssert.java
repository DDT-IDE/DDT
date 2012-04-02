package dtool.ast.declarations;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.expressions.Resolvable;
import dtool.ast.statements.IStatement;

public class DeclarationStaticAssert extends ASTNeoNode implements IStatement {

	public final Resolvable pred;
	public final Resolvable msg;
	
	public DeclarationStaticAssert(Resolvable pred, Resolvable msg, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.pred = pred; parentize(this.pred);
		this.msg = msg; parentize(this.msg);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, pred);
			TreeVisitor.acceptChildren(visitor, msg);
		}
		visitor.endVisit(this);
	}

}
