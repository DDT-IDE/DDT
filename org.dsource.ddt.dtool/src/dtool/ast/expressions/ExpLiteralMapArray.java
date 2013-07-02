package dtool.ast.expressions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.NodeListView;

public class ExpLiteralMapArray extends Expression {
	
	public final NodeListView<MapArrayLiteralKeyValue> entries;
	
	public ExpLiteralMapArray(NodeListView<MapArrayLiteralKeyValue> entries) {
		this.entries = parentize(assertNotNull(entries));
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
		cp.appendNodeList("[ ", entries, ", ", " ]");
	}
	
	
	public static class MapArrayLiteralKeyValue extends ASTNode {
		public final Expression key;
		public final Expression value;
		
		public MapArrayLiteralKeyValue(Expression key, Expression value) {
			this.key = parentize(assertNotNull(key));
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
			cp.append(key);
			cp.append(" : ");
			cp.append(value);
		}
	}
	
}