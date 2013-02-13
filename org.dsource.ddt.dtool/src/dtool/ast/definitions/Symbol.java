package dtool.ast.definitions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTVisitor;
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
	public void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(name);
	}
	
}