package dtool.ast.statements;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.LabelStatement;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.definitions.Symbol;
import dtool.descentadapter.DefinitionConverter;

public class StatementLabel extends Statement {

	public Symbol label;
	
	public StatementLabel(LabelStatement elem) {
		setSourceRange(elem);
		this.label = DefinitionConverter.convertId(elem.ident);
	}
	
	public StatementLabel(Symbol label) {
		this.label = label;
		if (this.label != null)
			this.label.setParent(this);
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
