package dtool.ast.definitions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import descent.internal.compiler.parser.IdentifierExp;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.NeoSourceRange;
import dtool.descentadapter.DefinitionConverter;

/** A Symbol is node wrapping an identifier, used only in DefUnits names.*/
public class Symbol extends ASTNeoNode {
	
	public String name;
	
	@Deprecated
	public Symbol(IdentifierExp id) {
		this(DefinitionConverter.convertId(id));
	}
	
	public Symbol(Symbol symbol) {
		this(symbol.name, symbol.getSourceRangeNeo());
	}
	
	public Symbol(String name, NeoSourceRange sourceRange) {
		assertNotNull(name);
		this.name = name;
		maybeSetSourceRange(sourceRange);
	}
	
	public Symbol(char[] name) {
		this(new String(name));
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