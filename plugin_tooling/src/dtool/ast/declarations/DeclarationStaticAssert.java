package dtool.ast.declarations;

import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import dtool.ast.expressions.Expression;
import dtool.ast.statements.IStatement;

public class DeclarationStaticAssert extends ASTNode implements IDeclaration, IStatement {
	
	public final Expression pred;
	public final Expression msg;
	
	public DeclarationStaticAssert(Expression pred, Expression msg) {
		this.pred = parentize(pred);
		this.msg = parentize(msg);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DECLARATION_STATIC_ASSERT;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, pred);
		acceptVisitor(visitor, msg);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("static assert(");
		cp.append(pred);
		cp.append(", ", msg);
		cp.append(");");
	}
	
}