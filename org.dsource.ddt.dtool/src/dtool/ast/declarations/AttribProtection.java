package dtool.ast.declarations;

import static dtool.util.NewUtils.assertNotNull_;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.ISourceRepresentation;

public class AttribProtection extends Attribute {
	
	public final Protection protection;
	
	public AttribProtection(Protection protection) {
		this.protection = assertNotNull_(protection);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.ATTRIB_PROTECTION;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		if (visitor.visit(this)) {
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.appendToken(protection);
	}
	
	public enum Protection implements ISourceRepresentation {
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