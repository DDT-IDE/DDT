package dtool.ast.expressions;

import static dtool.util.NewUtils.assertNotNull_;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;

public class ExpSlice extends Expression {
	
	public final Expression slicee;
	public final Expression startIndex;
	public final Expression endIndex;
	
	public ExpSlice(Expression slicee, Expression startIndex, Expression endIndex) {
		this.slicee = parentizeI(assertNotNull_(slicee));
		this.startIndex = parentize(startIndex);
		this.endIndex = parentize(endIndex);
		assertTrue((endIndex == null) || (startIndex != null));
	}
	
	public ExpSlice(Expression slicee) {
		this(slicee, null, null);
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
			TreeVisitor.acceptChildren(visitor, startIndex);
			TreeVisitor.acceptChildren(visitor, endIndex);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(slicee, "[");
		if(startIndex != null) {
			cp.append(startIndex);
			cp.append(" .. ", endIndex);
		}
		cp.append("]");
	}
}