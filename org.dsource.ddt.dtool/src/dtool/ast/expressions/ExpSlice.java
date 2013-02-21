package dtool.ast.expressions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.SourceRange;

public class ExpSlice extends Expression {
	
	public final Expression slicee;
	public final Expression from;
	public final Expression to;
	
	public ExpSlice(Expression slicee, Expression from, Expression to, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.slicee = parentizeI(slicee);
		this.from = parentize(from);
		this.to = parentize(to);
		assertTrue((to == null) || (from != null));
	}
	
	public ExpSlice(Expression slicee,SourceRange sourceRange) {
		this(slicee, null, null, sourceRange);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.EXP_SLICE;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, slicee);
			TreeVisitor.acceptChildren(visitor, from);
			TreeVisitor.acceptChildren(visitor, to);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.appendNode(slicee, "[");
		if(from != null) {
			cp.appendNode(from);
			cp.appendNode(" .. ", to);
		}
		cp.append("]");
	}
}