package dtool.ast.declarations;

import descent.internal.compiler.parser.LINK;
import descent.internal.compiler.parser.LinkDeclaration;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.statements.IStatement;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;

public class DeclarationLinkage extends DeclarationAttrib implements IStatement {

	public LINK linkage;
	
	public DeclarationLinkage(LinkDeclaration elem, ASTConversionContext convContex) {
		super(elem, elem.decl, convContex);
		this.linkage = elem.linkage;
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
