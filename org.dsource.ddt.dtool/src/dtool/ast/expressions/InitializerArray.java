package dtool.ast.expressions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.NodeListView;

public class InitializerArray extends Expression implements IInitializer {
	
	public final NodeListView<ArrayInitEntry> entries;
	
	public InitializerArray(NodeListView<ArrayInitEntry> indexes) {
		this.entries = parentize(assertNotNull(indexes));
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.INITIALIZER_ARRAY;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, entries);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.appendNodeList("[", entries, ", ", "]");
	}
	
	public static class ArrayInitEntry extends ASTNode {
		public final Expression index;
		public final IInitializer value;
		
		public ArrayInitEntry(Expression index, IInitializer value) {
			this.index = parentize(index);
			this.value = parentize(assertNotNull(value));
		}
		
		@Override
		public ASTNodeTypes getNodeType() {
			return ASTNodeTypes.ARRAY_INIT_ENTRY;
		}
		
		@Override
		public void visitChildren(IASTVisitor visitor) {
			acceptVisitor(visitor, index);
			acceptVisitor(visitor, value);
		}
		
		@Override
		public void toStringAsCode(ASTCodePrinter cp) {
			cp.append(index, " : ");
			cp.append(value);
		}
	}
	
}