package dtool.ast.definitions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.NeoSourceRange;
import dtool.ast.TokenInfo;

/** A Symbol is node wrapping an identifier, used only in DefUnits names.*/
public class Symbol extends ASTNeoNode {
	
	public String name;
	
	public Symbol(TokenInfo symbol) {
		this(symbol.value, symbol.getRange());
	}
	
	public Symbol(String name, NeoSourceRange sourceRange) {
		assertNotNull(name);
		this.name = name;
		initSourceRange(sourceRange);
	}
	
	public Symbol(String name) {
		this(name, null);
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Symbol) && name.equals(((Symbol) obj).name);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
	@Override
	public String toStringAsElement() {
		return name;
	}
	
}