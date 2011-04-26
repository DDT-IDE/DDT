package dtool.ast.definitions;

import dtool.ast.SourceRange;
import dtool.ast.TokenInfo;


/**
 * A Symbol that is the name of a DefUnit, and that knows how to get
 * that DefUnit. Its node parent must be the corresponding DefUnit.
 */
public class DefSymbol extends Symbol {
	
	public DefSymbol(TokenInfo id, DefUnit parent) {
		super(id);
		setParent(parent);
	}
	
	public DefSymbol(String id,  SourceRange sourceRange, DefUnit parent) {
		super(id, sourceRange);
		setParent(parent);
	}
	
	protected DefSymbol(String id) {
		super(id, null);
	}
	
	public DefUnit getDefUnit() {
		return (DefUnit) super.getParent();
	}
	
}
