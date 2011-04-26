package dtool.ast.definitions;


/**
 * A Symbol that is the name of a DefUnit, and that knows how to get
 * that DefUnit. Its node parent must be the corresponding DefUnit.
 */
public class DefSymbol extends Symbol {
	
	public DefSymbol(Symbol id, DefUnit parent) {
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
