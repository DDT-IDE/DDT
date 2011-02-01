package dtool.ast.declarations;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.InvariantDeclaration;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.statements.BlockStatement;
import dtool.ast.statements.Statement;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;

public class DeclarationInvariant extends ASTNeoNode {

	public BlockStatement body;
	
	public DeclarationInvariant(InvariantDeclaration elem, ASTConversionContext convContext) {
		setSourceRange(elem);
		this.body = (BlockStatement) Statement.convert(elem.fbody, convContext);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, body);
		}
		visitor.endVisit(this);
	}

}
