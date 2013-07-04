package dtool.ast.declarations;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.ISourceRepresentation;

public class AttribProtection extends Attribute {
	
	public final EProtection protection;
	
	public AttribProtection(EProtection protection) {
		this.protection = assertNotNull(protection);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.ATTRIB_PROTECTION;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.appendToken(protection);
	}
	
	public enum EProtection implements ISourceRepresentation {
	    PRIVATE,
	    PACKAGE,
	    PROTECTED,
	    PUBLIC,
	    EXPORT,
	    ;
	    
		@Override
		public String getSourceValue() {
			return toString().toLowerCase();
		}
	}
	
}