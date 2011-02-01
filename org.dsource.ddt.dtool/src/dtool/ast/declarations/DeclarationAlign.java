package dtool.ast.declarations;

import descent.internal.compiler.parser.AlignDeclaration;
import dtool.ast.IASTNeoVisitor;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;

public class DeclarationAlign extends DeclarationAttrib {
	
	public long alignnum;

	public DeclarationAlign(AlignDeclaration elem, ASTConversionContext convContex) {
		super(elem, elem.decl, convContex);
		this.alignnum = elem.salign;
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
