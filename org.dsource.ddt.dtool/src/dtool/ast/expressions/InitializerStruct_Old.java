package dtool.ast.expressions;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTVisitor;
import dtool.ast.references.RefIdentifier;
import dtool.util.ArrayView;

public class InitializerStruct_Old extends Initializer {
	
	public final ArrayView<RefIdentifier> indexes;
	public final ArrayView<Initializer> values;
	
	public InitializerStruct_Old(ArrayView<RefIdentifier> indexes, ArrayView<Initializer> values) {
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