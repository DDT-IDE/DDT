package dtool.ast.definitions;

import descent.internal.compiler.parser.IdentifierExp;

/**
 * A Symbol that is the name of a DefUnit, and that knows how to get
 * that DefUnit. Its node parent must be the DefUnit.
 */
public class DefSymbol extends Symbol {
	
	public DefSymbol(IdentifierExp id, DefUnit parent) {
		super(id);
		setParent(parent);
	}
	
	protected DefSymbol(String id) {
		super(id);
	}
	
	protected DefSymbol(String id, DefUnit parent) {
		super(id);
		setParent(parent);
	}
	
	public DefUnit getDefUnit() {
		return (DefUnit) super.getParent();
	}
	
}
