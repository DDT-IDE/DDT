package dtool.ast.statements;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.definitions.Symbol;

public class StatementLabel extends Statement {

	public final Symbol label;
	
	public StatementLabel(Symbol label, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.label = label; parentize(this.label);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, label);
		}
		visitor.endVisit(this);
	}


}
