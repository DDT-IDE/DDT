package dtool.ast.declarations;

import descent.internal.compiler.parser.LINK;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.statements.IStatement;

public class DeclarationLinkage extends DeclarationAttrib implements IStatement {
	
	public final LINK linkage;
	
	public DeclarationLinkage(LINK link, ASTNeoNode[] decls, boolean hasCurlies, SourceRange sourceRange) {
		super(new NodeList(decls, hasCurlies), sourceRange);
		this.linkage = link;
	}

	public DeclarationLinkage(LINK link, NodeList decls, SourceRange sourceRange) {
		super(decls, sourceRange);
		this.linkage = link;
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
		return "[extern("+linkage+")]";
	}

}
