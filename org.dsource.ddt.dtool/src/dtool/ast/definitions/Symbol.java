package dtool.ast.definitions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;

/** A Symbol is node wrapping an identifier. */
public class Symbol extends ASTNeoNode {
	
	public final String name;
	
	public Symbol(String name, SourceRange sourceRange) {
		assertNotNull(name);
		this.name = name;
		initSourceRange(sourceRange);
	}
	
	@Override
	public final boolean equals(Object obj) {
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