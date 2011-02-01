package dtool.ast.statements;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.GotoStatement;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.definitions.Symbol;

public class StatementGoto extends Statement {

	public Symbol label;
	
	public StatementGoto(GotoStatement elem) {
		convertNode(elem);
		this.label = new Symbol(elem.ident);
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
