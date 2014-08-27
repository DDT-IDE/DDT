package dtool.ast.declarations;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.parser.common.IToken;

/**
 * Align declaration 
 * 
 * Technicaly DMD doesn't accept this declaration as a statement, but structurally we allow it,
 * even though a syntax or semantic error may still be issued.
 * 
 */
public class AttribAlign extends Attribute {
	
	public final IToken alignNum;
	
	public AttribAlign(IToken alignNum) {
		this.alignNum = alignNum;
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.ATTRIB_ALIGN;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("align");
		if(alignNum != null) {
			cp.appendStrings("(", alignNum.getSourceValue(), ")");
		}
	}
	
}