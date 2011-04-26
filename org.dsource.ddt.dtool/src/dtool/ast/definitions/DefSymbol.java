package dtool.ast.definitions;

import dtool.ast.NeoSourceRange;
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
	
	protected DefSymbol(String id) {
		super(id, null);
	}
	
	protected DefSymbol(String id, DefUnit parent) {
		this(id, null, parent);
	}
	
	protected DefSymbol(String id,  NeoSourceRange sourceRange, DefUnit parent) {
		super(id, sourceRange);
		setParent(parent);
	}
	
	public DefUnit getDefUnit() {
		return (DefUnit) super.getParent();
	}
	
}
