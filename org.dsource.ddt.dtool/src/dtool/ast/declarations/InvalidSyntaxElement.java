package dtool.ast.declarations;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTVisitor;
import dtool.ast.SourceRange;
import dtool.ast.statements.IStatement;
import dtool.parser.Token;

public class InvalidSyntaxElement extends ASTNeoNode implements IStatement {
	
	public final Token badToken;
	public final ASTNeoNode node;
	
	public InvalidSyntaxElement(Token badToken) {
		super(badToken.getSourceRange());
		this.badToken = badToken;
		this.node = null;
	}
	
	public InvalidSyntaxElement(ASTNeoNode node, SourceRange sourceRange) {
		super(sourceRange);
		this.badToken = null;
		this.node = parentize(node);
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
		if(badToken != null) {
			cp.append(badToken);
		} else {
			cp.appendNode(node);
		}
	}
	
}