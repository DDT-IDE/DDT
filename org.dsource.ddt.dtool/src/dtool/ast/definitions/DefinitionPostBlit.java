package dtool.ast.definitions;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.PostBlitDeclaration;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.statements.IStatement;
import dtool.ast.statements.Statement;
import dtool.descentadapter.DescentASTConverter.ASTConversionContext;

public class DefinitionPostBlit extends ASTNeoNode {
	
	public IStatement fbody;
	
	public DefinitionPostBlit(PostBlitDeclaration elem, ASTConversionContext convContext) {
		convertNode(elem);
		this.fbody = Statement.convert(elem.fbody, convContext);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, fbody);
		}
		visitor.endVisit(this);
	}
	
}
