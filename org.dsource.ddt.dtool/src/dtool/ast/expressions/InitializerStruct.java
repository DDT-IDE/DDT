package dtool.ast.expressions;

import static dtool.util.NewUtils.assertNotNull_;
import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.references.RefIdentifier;
import dtool.util.ArrayView;

public class InitializerStruct extends Initializer {
	
	public final ArrayView<StructInitEntry> entries;
	public final boolean hasEndingComma;
	
	public InitializerStruct(ArrayView<StructInitEntry> indexes, boolean hasEndingComma) {
		this.entries = parentize(indexes);
		this.hasEndingComma = hasEndingComma;
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.INITIALIZER_STRUCT;
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
		cp.append("{");
		cp.appendNodeList(entries, ", ", hasEndingComma);
		cp.append("}");
	}
	
	public static class StructInitEntry extends ASTNode {
		public final RefIdentifier member;
		public final Initializer value;
		
		public StructInitEntry(RefIdentifier member, Initializer value) {
			this.member = parentize(member);
			this.value = parentize(assertNotNull_(value));
		}
		
		@Override
		public ASTNodeTypes getNodeType() {
			return ASTNodeTypes.STRUCT_INIT_ENTRY;
		}
		
		@Override
		public void accept0(IASTVisitor visitor) {
			boolean children = visitor.visit(this);
			if (children) {
				TreeVisitor.acceptChildren(visitor, member);
				TreeVisitor.acceptChildren(visitor, value);
			}
			visitor.endVisit(this);	 
		}
		
		@Override
		public void toStringAsCode(ASTCodePrinter cp) {
			cp.appendNode(member, " : ");
			cp.appendNode(value);
		}
	}
	
}