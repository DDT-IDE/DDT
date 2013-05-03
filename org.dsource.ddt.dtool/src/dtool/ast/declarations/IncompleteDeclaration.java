package dtool.ast.declarations;

import static dtool.util.NewUtils.assertNotNull_;
import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNeoNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.statements.IStatement;

public class IncompleteDeclaration extends ASTNeoNode implements IStatement {
	
	public final ASTNeoNode node;
	public final boolean consumedSemiColon;
	
	public IncompleteDeclaration(ASTNeoNode node, boolean consumedSemiColon) {
		this.node = parentize(assertNotNull_(node));
		this.consumedSemiColon = consumedSemiColon;
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.INCOMPLETE_DECLARATION;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, node);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.appendNode(node);
		cp.append(consumedSemiColon, ";");
	}
	
}