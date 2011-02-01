package dtool.ast.definitions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import melnorme.utilbox.core.Assert;
import descent.internal.compiler.parser.IdentifierExp;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;

/** A Symbol is node wrapping an identifier, used only in DefUnits names.*/
public class Symbol extends ASTNeoNode {
	public String name;

	public Symbol(IdentifierExp id) {
		Assert.isTrue(id.getClass() == IdentifierExp.class);
		setSourceRange(id);
		this.name = new String(id.ident);
	}

	public Symbol(String name) {
		assertNotNull(name);
		this.name = name;
	}
	
	public Symbol(char[] name) {
		this.name = new String(name);
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