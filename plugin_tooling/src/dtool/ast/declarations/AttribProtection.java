package dtool.ast.declarations;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.lang.tooling.parser.ISourceRepresentation;

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