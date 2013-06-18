package dtool.ast.declarations;

import static dtool.util.NewUtils.assertNotNull_;
import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.definitions.Symbol;

public class AttribAt extends Attribute {
	
	public final Symbol attribId;
	
	public AttribAt(Symbol attribId) {
		this.attribId = parentize(assertNotNull_(attribId));
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.ATTRIB_AT;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, attribId);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("@", attribId);
	}
	
}