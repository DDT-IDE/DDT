package dtool.ast.declarations;

import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;

public class DeclarationAlign extends DeclarationAttrib {
	
	public final long alignnum;

	public DeclarationAlign(long align, ASTNeoNode[] decls, boolean hasCurlies, SourceRange sourceRange) {
		super(new dtool.ast.declarations.NodeList(decls, hasCurlies), sourceRange);
		this.alignnum = align;
	}
	
	public DeclarationAlign(long align, dtool.ast.declarations.NodeList decls, SourceRange sourceRange) {
		super(decls, sourceRange);
		this.alignnum = align;
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			acceptBodyChildren(visitor);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public String toStringAsElement() {
		return "[align("+alignnum+")]";
	}

}
