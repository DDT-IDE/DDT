package dtool.ast.expressions;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.util.ArrayView;

public class ExpTraits extends Expression {
	
	public final ArrayView<ASTNeoNode> args;
	public final char[] traitsKeyword;
	
	public ExpTraits(char[] traitsKeyword, ArrayView<ASTNeoNode> args, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.traitsKeyword = traitsKeyword;
		this.args = parentize(args);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, args);
		}
		visitor.endVisit(this);	 
	}
	
}