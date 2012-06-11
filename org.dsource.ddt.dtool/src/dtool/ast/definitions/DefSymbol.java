package dtool.ast.definitions;

import dtool.ast.SourceRange;


/**
 * A Symbol that is the name of a DefUnit, and that knows how to get
 * that DefUnit. Its node parent must be the corresponding DefUnit.
 */
public class DefSymbol extends Symbol {
	
	public DefSymbol(String id,  SourceRange sourceRange, DefUnit parent) {
		this(id, sourceRange);
		setParent(parent);
	}
	
	protected DefSymbol(String id, SourceRange sourceRange) {
		super(id, sourceRange);
	}
	
	public DefUnit getDefUnit() {
		return (DefUnit) super.getParent();
	}
	
}
