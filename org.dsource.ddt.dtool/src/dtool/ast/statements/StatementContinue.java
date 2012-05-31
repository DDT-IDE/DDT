package dtool.ast.statements;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.ContinueStatement;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.definitions.Symbol;
import dtool.descentadapter.DefinitionConverter;

public class StatementContinue extends Statement {

	public Symbol id;

	public StatementContinue(ContinueStatement elem) {
		convertNode(elem);
		if(elem.ident != null)
			this.id = DefinitionConverter.convertId(elem.ident);
	}
	
	public StatementContinue(Symbol id) {
		this.id = id;
		if (this.id != null)
			this.id.setParent(this);
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
