package dtool.ast.references;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.parser.IToken;

public class RefPrimitive extends NamedReference {
	
	public final IToken primitive;
	
	public RefPrimitive(IToken primitiveToken) {
		this.primitive = assertNotNull(primitiveToken);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.REF_PRIMITIVE;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.appendToken(primitive);
	}
	
	@Override
	public String getCoreReferenceName() {
		return primitive.getSourceValue();
	}
	
	@Override
	public boolean isMissingCoreReference() {
		return false;
	}
	
}