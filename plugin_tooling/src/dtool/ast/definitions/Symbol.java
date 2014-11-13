package dtool.ast.definitions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;

/** A Symbol is node wrapping an identifier. */
public class Symbol extends ASTNode {
	
	public final String name;
	
	public Symbol(String name) {
		assertNotNull(name);
		this.name = name;
	}
	
	@Override
	public final boolean equals(Object obj) {
		return (obj instanceof Symbol) && name.equals(((Symbol) obj).name);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.SYMBOL;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(name);
	}
	
}