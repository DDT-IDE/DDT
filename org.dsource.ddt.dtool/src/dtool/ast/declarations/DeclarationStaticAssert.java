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
		this.pred = parentize(pred);
		this.msg = parentize(msg);
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