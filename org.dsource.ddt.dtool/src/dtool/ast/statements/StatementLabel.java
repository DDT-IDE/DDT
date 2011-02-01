package dtool.ast.statements;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.LabelStatement;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.definitions.Symbol;

public class StatementLabel extends Statement {

	public Symbol label;
	
	public StatementLabel(LabelStatement elem) {
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
