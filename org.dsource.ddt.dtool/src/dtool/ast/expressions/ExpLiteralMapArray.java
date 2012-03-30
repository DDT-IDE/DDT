package dtool.ast.expressions;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.definitions.ArrayView;

public class ExpLiteralMapArray extends Expression {

	public final ArrayView<Resolvable> keys;
	public final ArrayView<Resolvable> values;
	
	public ExpLiteralMapArray(Resolvable[] keys, Resolvable[] values, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.keys = new ArrayView<Resolvable>(keys); parentize(this.keys);
		this.values = new ArrayView<Resolvable>(values); parentize(this.values);
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, keys);
			TreeVisitor.acceptChildren(visitor, values);
		}
		visitor.endVisit(this);	 
	}

}
