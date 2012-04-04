package dtool.ast.statements;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.definitions.Symbol;

public class StatementContinue extends Statement {

	public final Symbol id;

	public StatementContinue(Symbol id, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.id = id; parentize(this.id);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, id);
		}
		visitor.endVisit(this);
	}

}
