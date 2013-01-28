package dtool.ast.declarations;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.statements.IStatement;
import dtool.util.ArrayView;

@Deprecated
public class InvalidSyntaxDeclaration_Old extends ASTNeoNode implements IStatement {
	
	public final ArrayView<ASTNeoNode> genericChildren;
	
	public InvalidSyntaxDeclaration_Old(SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.genericChildren = null;
	}
	
	public InvalidSyntaxDeclaration_Old(SourceRange sourceRange, ArrayView<ASTNeoNode> children) {
		initSourceRange(sourceRange);
		this.genericChildren = parentize(children, true);
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