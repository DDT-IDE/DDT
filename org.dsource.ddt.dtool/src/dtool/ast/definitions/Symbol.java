package dtool.ast.definitions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;

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
	public void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(name);
	}
	
}