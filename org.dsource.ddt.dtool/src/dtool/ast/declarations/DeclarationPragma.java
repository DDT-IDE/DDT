package dtool.ast.declarations;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.definitions.ArrayView;
import dtool.ast.definitions.Symbol;
import dtool.ast.expressions.Resolvable;
import dtool.ast.statements.IStatement;

public class DeclarationPragma extends DeclarationAttrib implements IStatement {
	
	public final Symbol ident;
	public final ArrayView<Resolvable> expressions;
	
	public DeclarationPragma(Symbol id, Resolvable[] expressions, NodeList body, SourceRange sourceRange) {
		super(body, sourceRange);
		this.ident = id; parentize(this.ident);
		if(expressions != null) {
			this.expressions = new ArrayView<Resolvable>(expressions); parentize(this.expressions);
		} else {
			this.expressions = null;
		}
	}

	public DeclarationPragma(Symbol id, Resolvable[] expressions, ASTNeoNode[] body, boolean hasCurlies, SourceRange sourceRange) {
		super(new NodeList(body, hasCurlies), sourceRange);
		this.ident = id; parentize(this.ident);
		if (expressions != null) {
			this.expressions = new ArrayView<Resolvable>(expressions); parentize(this.expressions);
		} else {
			this.expressions = null;
		}
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, ident);
			TreeVisitor.acceptChildren(visitor, expressions);
			acceptBodyChildren(visitor);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public String toStringAsElement() {
		return "[pragma("+ident+",...)]";
	}
}
