package dtool.ast.statements;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.definitions.ArrayView;
import dtool.ast.expressions.Resolvable;

public class StatementCase extends Statement {

	public final Resolvable exp;
	public final IStatement st;
	
	public final ArrayView<Resolvable> expList;
	public final ArrayView<IStatement> stList;
	
	public StatementCase(Resolvable[] expList, IStatement[] stList, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.expList = new ArrayView<Resolvable>(expList); parentize(this.expList);
		this.stList = new ArrayView<IStatement>(stList); parentize(this.stList);
		this.exp = null;
		this.st = null;
	}
	
	public StatementCase(Resolvable exp, IStatement st, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.exp = exp; parentize(this.exp);
		this.st = st; parentize(this.st);
		this.expList = null;
		this.stList = null;
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, exp);
			TreeVisitor.acceptChildren(visitor, st);
		}
		visitor.endVisit(this);
	}

}
