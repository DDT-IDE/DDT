package dtool.ast.declarations;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;

/**
 * Align declaration 
 * 
 * Technicaly DMD doesn't accept this declaration as a statement, but structurally we allow it,
 * even though a syntax or semantic error may still be issued.
 * 
 */
public class AttribAlign extends Attribute {
	
	public final String alignNum;
	
	public AttribAlign(String alignNum) {
		this.alignNum = alignNum;
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.ATTRIB_ALIGN;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		if (visitor.visit(this)) {
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("align");
		if(alignNum != null) {
			cp.appendStrings("(", alignNum, ")");
		}
	}
	
}