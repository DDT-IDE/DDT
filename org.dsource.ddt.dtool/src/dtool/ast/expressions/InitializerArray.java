package dtool.ast.expressions;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.util.ArrayView;

public class InitializerArray extends Initializer {
	
	public final ArrayView<Resolvable> indexes;
	public final ArrayView<Initializer> values;
	
	public InitializerArray(ArrayView<Resolvable> indexes, ArrayView<Initializer> values, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.indexes = indexes; parentize(this.indexes, true);
		this.values = values; parentize(this.values);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, indexes);
			TreeVisitor.acceptChildren(visitor, values);
		}
		visitor.endVisit(this);
	}

}
