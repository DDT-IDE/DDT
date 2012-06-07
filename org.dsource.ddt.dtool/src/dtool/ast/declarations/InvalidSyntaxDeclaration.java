package dtool.ast.declarations;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.statements.IStatement;
import dtool.util.ArrayView;

public class InvalidSyntaxDeclaration extends ASTNeoNode implements IStatement {
	
	public ArrayView<ASTNeoNode> genericChildren;
	
	public InvalidSyntaxDeclaration(SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.genericChildren = null;
	}
	
	public InvalidSyntaxDeclaration(SourceRange sourceRange, ArrayView<ASTNeoNode> children) {
		initSourceRange(sourceRange);
		this.genericChildren = children; // BUG here parentize(genericChildren);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, genericChildren);
		}
		visitor.endVisit(this);
	}
	
}
