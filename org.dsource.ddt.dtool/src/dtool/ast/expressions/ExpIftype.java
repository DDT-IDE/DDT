package dtool.ast.expressions;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.TOK;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.references.Reference;

public class ExpIftype extends Expression {
	
	public final Reference arg;
	public final TOK tok;
	public final Reference specType;
	
	public ExpIftype(Reference arg, TOK tok, Reference specType, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.tok = tok;
		this.arg = parentize(arg);
		this.specType = parentize(specType);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, arg);
			TreeVisitor.acceptChildren(visitor, specType);
		}
		visitor.endVisit(this);
	}
	
}