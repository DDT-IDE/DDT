package dtool.ast.expressions;

import static dtool.util.NewUtils.assertNotNull_;
import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNeoNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.util.ArrayView;

public class ExpLiteralMapArray extends Expression {
	
	public final ArrayView<MapArrayLiteralKeyValue> entries;
	
	public ExpLiteralMapArray(ArrayView<MapArrayLiteralKeyValue> entries) {
		this.entries = parentize(assertNotNull_(entries));
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.EXP_LITERAL_MAPARRAY;
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
		cp.append("[ ");
		cp.appendNodeList(entries, ", ");
		cp.append(" ]");
	}
	
	
	public static class MapArrayLiteralKeyValue extends ASTNeoNode {
		public final Expression key;
		public final Expression value;
		
		public MapArrayLiteralKeyValue(Expression key, Expression value) {
			this.key = parentize(key);
			this.value = parentize(value);
		}
		
		@Override
		public ASTNodeTypes getNodeType() {
			return ASTNodeTypes.MAPARRAY_ENTRY;
		}
		
		@Override
		public void accept0(IASTVisitor visitor) {
			boolean children = visitor.visit(this);
			if (children) {
				TreeVisitor.acceptChildren(visitor, key);
				TreeVisitor.acceptChildren(visitor, value);
			}
			visitor.endVisit(this);	 
		}
		
		@Override
		public void toStringAsCode(ASTCodePrinter cp) {
			cp.appendNode(key);
			cp.append(" : ");
			cp.appendNode(value);
		}
	}
	
}