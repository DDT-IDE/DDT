package dtool.ast.statements;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.definitions.ArrayView;

public class StatementDefault extends Statement {

	public final IStatement st;
	public final ArrayView<IStatement> stList;
	
	public StatementDefault(IStatement[] stList, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.stList = new ArrayView<IStatement>(stList); parentize(this.stList);
		this.st = null;
	}
	
	public StatementDefault(IStatement st, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.st = st; parentize(this.st);
		this.stList = null;
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, st);
		}
		visitor.endVisit(this);
	}

}
