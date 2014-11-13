package dtool.ast.expressions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast.util.NodeListView;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import dtool.ast.references.RefIdentifier;

public class InitializerStruct extends Initializer {
	
	public final NodeListView<StructInitEntry> entries;
	
	public InitializerStruct(NodeListView<StructInitEntry> indexes) {
		this.entries = parentize(indexes);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.INITIALIZER_STRUCT;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, entries);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.appendNodeList("{", entries, ", ", "}");
	}
	
	public static class StructInitEntry extends ASTNode {
		public final RefIdentifier member;
		public final IInitializer value;
		
		public StructInitEntry(RefIdentifier member, IInitializer value) {
			this.member = parentize(member);
			this.value = parentize(assertNotNull(value));
		}
		
		@Override
		public ASTNodeTypes getNodeType() {
			return ASTNodeTypes.STRUCT_INIT_ENTRY;
		}
		
		@Override
		public void visitChildren(IASTVisitor visitor) {
			acceptVisitor(visitor, member);
			acceptVisitor(visitor, value);
		}
		
		@Override
		public void toStringAsCode(ASTCodePrinter cp) {
			cp.append(member, " : ");
			cp.append(value);
		}
	}
	
}