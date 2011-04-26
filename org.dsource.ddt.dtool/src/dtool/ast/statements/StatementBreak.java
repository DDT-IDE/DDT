package dtool.ast.statements;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.BreakStatement;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.definitions.Symbol;
import dtool.descentadapter.DefinitionConverter;

public class StatementBreak extends Statement {

	public Symbol id;
	
	public StatementBreak(BreakStatement elem) {
		convertNode(elem);
		if(elem.ident != null)
			this.id = DefinitionConverter.convertId(elem.ident);
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
