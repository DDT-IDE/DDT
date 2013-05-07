package dtool.ast.expressions;

import static dtool.util.NewUtils.assertNotNull_;
import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.util.ArrayView;

public class InitializerArray extends Initializer {
	
	public final ArrayView<ArrayInitEntry> entries;
	public final boolean hasEndingComma;
	
	public InitializerArray(ArrayView<ArrayInitEntry> indexes, boolean hasEndingComma) {
		this.entries = parentize(indexes);
		this.hasEndingComma = hasEndingComma;
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.INITIALIZER_ARRAY;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, entries);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("[");
		cp.appendNodeList(entries, ", ", hasEndingComma);
		cp.append("]");
	}
	
	public static class ArrayInitEntry extends ASTNode {
		public final Expression index;
		public final Initializer value;
		
		public ArrayInitEntry(Expression index, Initializer value) {
			this.index = parentize(index);
			this.value = parentize(assertNotNull_(value));
		}
		
		@Override
		public ASTNodeTypes getNodeType() {
			return ASTNodeTypes.ARRAY_INIT_ENTRY;
		}
		
		@Override
		public void accept0(IASTVisitor visitor) {
			boolean children = visitor.visit(this);
			if (children) {
				TreeVisitor.acceptChildren(visitor, index);
				TreeVisitor.acceptChildren(visitor, value);
			}
			visitor.endVisit(this);	 
		}
		
		@Override
		public void toStringAsCode(ASTCodePrinter cp) {
			cp.appendNode(index, " : ");
			cp.appendNode(value);
		}
	}
	
}