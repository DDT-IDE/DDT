package dtool.ast.declarations;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.definitions.Symbol;

public class AttribAtKeyword extends Attribute {
	
	public final Symbol attribId;
	
	public AttribAtKeyword(Symbol attribId) {
		this.attribId = parentize(assertNotNull(attribId));
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.ATTRIB_AT_KEYWORD;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, attribId);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("@", attribId);
	}
	
}