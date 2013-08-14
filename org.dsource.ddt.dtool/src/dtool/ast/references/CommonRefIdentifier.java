package dtool.ast.references;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import dtool.ast.ASTCodePrinter;
import dtool.ast.IASTVisitor;
import dtool.resolver.PrefixDefUnitSearch;

public abstract class CommonRefIdentifier extends NamedReference {
	
	protected final String identifier;
	
	public CommonRefIdentifier(String identifier) {
		this.identifier = identifier;
		assertTrue(identifier == null || identifier.length() > 0); 
		assertTrue(getDenulledIdentifier().indexOf(' ') == -1);
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(identifier);
	}
	
	@Override
	public String getCoreReferenceName() { 
		return identifier;
	}
	
	public boolean isMissing() {
		return identifier == null;
	}
	
	public String getDenulledIdentifier() {
		return identifier == null ? "" : identifier;
	}
	
	@Override
	public void performPrefixSearch(PrefixDefUnitSearch prefixSearch, String fullSource) {
		prefixSearch.setupPrefixedSearchOptions(getStartPos(), getDenulledIdentifier());
		doSearch(prefixSearch);
	}
	
}