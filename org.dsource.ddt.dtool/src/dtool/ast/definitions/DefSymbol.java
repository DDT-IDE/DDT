package dtool.ast.definitions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;


/**
 * A Symbol that is the name of a DefUnit, and that knows how to get
 * that DefUnit. Its node parent must be the corresponding DefUnit.
 */
public class DefSymbol extends Symbol {
	
	public DefSymbol(String id) {
		super(id);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.SYMBOL;
	}
	
	@Override
	public void setParent(ASTNode parent) {
		checkParent(parent);
		super.setParent(parent);
	}
	
	public void checkParent(ASTNode parent) {
		assertTrue(parent instanceof DefUnit);
	}
	
	public DefUnit getDefUnit() {
		return (DefUnit) getParent();
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(name);
	}
}