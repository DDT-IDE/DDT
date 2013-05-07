package dtool.ast.declarations;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.expressions.Expression;

public class DeclarationStaticIf extends AbstractConditionalDeclaration {
	
	public final Expression exp;
	
	public DeclarationStaticIf(Expression exp, AttribBodySyntax bodySyntax, ASTNode thenBody, 
		ASTNode elseBody) {
		super(bodySyntax, thenBody, elseBody);
		this.exp = parentize(exp);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DECLARATION_STATIC_IF;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, exp);
			TreeVisitor.acceptChildren(visitor, body);
			TreeVisitor.acceptChildren(visitor, elseBody);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("static if ");
		cp.appendNode("(", exp, ")");
		toStringAsCodeBodyAndElseBody(cp);
	}
	
}