package dtool.ast.declarations;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.declarations.DeclarationAttrib.AttribBodySyntax;
import dtool.ast.expressions.Expression;
import dtool.ast.statements.IStatement;

public class DeclarationStaticIf extends AbstractConditionalDeclaration {
	
	public final Expression exp;
	
	public DeclarationStaticIf(Expression exp, AttribBodySyntax bodySyntax, ASTNode thenBody, 
		ASTNode elseBody) {
		super(bodySyntax, thenBody, elseBody);
		this.exp = parentize(exp);
	}
	
	public DeclarationStaticIf(Expression exp, IStatement thenBody, IStatement elseBody) {
		super(thenBody, elseBody);
		this.exp = parentize(exp);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DECLARATION_STATIC_IF;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, exp);
		acceptVisitor(visitor, body);
		acceptVisitor(visitor, elseBody);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("static if ");
		cp.append("(", exp, ")");
		toStringAsCodeBodyAndElseBody(cp);
	}
	
}