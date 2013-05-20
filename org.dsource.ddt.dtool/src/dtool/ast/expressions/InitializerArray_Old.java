package dtool.ast.expressions;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTVisitor;
import dtool.util.ArrayView;

@Deprecated
public class InitializerArray_Old extends Initializer {
	
	public final ArrayView<Resolvable> indexes;
	public final ArrayView<Initializer> values;
	
	public InitializerArray_Old(ArrayView<Resolvable> indexes, ArrayView<Initializer> values) {
		this.indexes = parentize(indexes, true);
		this.values = parentize(values);
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, indexes);
			TreeVisitor.acceptChildren(visitor, values);
		}
		visitor.endVisit(this);
	}
	
}