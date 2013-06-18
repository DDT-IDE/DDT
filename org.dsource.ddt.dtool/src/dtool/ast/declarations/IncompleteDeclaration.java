package dtool.ast.declarations;

import static dtool.util.NewUtils.assertNotNull_;
import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.references.Reference;
import dtool.util.ArrayView;

/**
 * Represents an incomplete var or function declaration (where the defId is missing).
 */
public class IncompleteDeclaration extends ASTNode implements IDeclaration {
	
	public final ArrayView<Attribute> attributes;
	public final Reference ref;
	
	public IncompleteDeclaration(ArrayView<Attribute> attributes, Reference ref) {
		this.attributes = parentize(attributes);
		this.ref = parentize(assertNotNull_(ref));
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.INCOMPLETE_DECLARATION;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, attributes);
			TreeVisitor.acceptChildren(visitor, ref);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.appendList(attributes, " ", true);
		cp.append(ref);
		cp.append(";");
	}
	
}