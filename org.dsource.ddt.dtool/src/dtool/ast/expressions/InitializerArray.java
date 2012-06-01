package dtool.ast.expressions;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.definitions.ArrayView;

public class InitializerArray extends Initializer {
	
	public final ArrayView<Resolvable> indexes;
	public final ArrayView<Initializer> values;
	
	public InitializerArray(Resolvable[] indexes, Initializer[] values, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.indexes = new ArrayView<Resolvable>(indexes); parentize(this.indexes);
		this.values = new ArrayView<Initializer>(values); parentize(this.values);
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
