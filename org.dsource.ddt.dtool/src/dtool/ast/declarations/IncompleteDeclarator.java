package dtool.ast.declarations;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.references.Reference;

/**
 * Represents an incomplete var or function declaration (where the defId is missing).
 */
public class IncompleteDeclarator extends ASTNode implements IDeclaration {
	
	public final Reference ref;
	
	public IncompleteDeclarator(Reference ref) {
		this.ref = parentize(assertNotNull(ref));
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.INCOMPLETE_DECLARATOR;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, ref);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(ref);
		cp.append(";");
	}
	
}