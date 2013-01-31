package dtool.ast.declarations;

import static dtool.util.NewUtils.assertNotNull_;
import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.statements.IStatement;

public class InvalidDeclaration extends ASTNeoNode implements IStatement {
	
	public final ASTNeoNode node;
	public final boolean consumedSemiColon;
	
	public InvalidDeclaration(ASTNeoNode node, boolean consumedSemiColon, SourceRange sourceRange) {
		this.node = parentize(assertNotNull_(node));
		this.consumedSemiColon = consumedSemiColon;
		initSourceRange(sourceRange);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, node);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.appendNode(node);
		if(consumedSemiColon) {
			cp.append(";");
		}
	}
	
}