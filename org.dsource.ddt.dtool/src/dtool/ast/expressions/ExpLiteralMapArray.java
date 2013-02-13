package dtool.ast.expressions;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTVisitor;
import dtool.ast.SourceRange;
import dtool.util.ArrayView;

public class ExpLiteralMapArray extends Expression {
	
	public final ArrayView<Resolvable> keys;
	public final ArrayView<Resolvable> values;
	
	public ExpLiteralMapArray(ArrayView<Resolvable> keys, ArrayView<Resolvable> values, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.keys = parentize(keys);
		this.values = parentize(values);
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, keys);
			TreeVisitor.acceptChildren(visitor, values);
		}
		visitor.endVisit(this);	 
	}
	
}