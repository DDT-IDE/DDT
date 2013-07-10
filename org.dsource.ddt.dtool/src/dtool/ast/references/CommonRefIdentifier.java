package dtool.ast.references;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import dtool.ast.ASTCodePrinter;
import dtool.ast.IASTVisitor;

public abstract class CommonRefIdentifier extends NamedReference {
	
	protected final String identifier;
	
	public CommonRefIdentifier(String identifier) {
		this.identifier = identifier;
		assertTrue(identifier == null || identifier.length() > 0); 
		assertTrue(getIdString().indexOf(' ') == -1);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(identifier);
	}
	
	@Override
	public boolean syntaxIsMissingIdentifier() {
		return isMissing();
	}

	public boolean isMissing() {
		return identifier == null;
	}

	public String getIdString() {
		return identifier == null ? "" : identifier;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
	}
	
}