package dtool.ast.statements;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;

public class StatementIfVar extends Statement {
	
	public final VariableDefWithInit conditionVar;
	public final IStatement thenBody;
	public final IStatement elseBody;
	
	public StatementIfVar(VariableDefWithInit conditionVar, IStatement thenBody, IStatement elseBody) {
		this.conditionVar = parentize(assertNotNull(conditionVar));
		this.thenBody = parentizeI(thenBody);
		this.elseBody = parentizeI(elseBody);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.STATEMENT_IF_VAR;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, conditionVar);
		acceptVisitor(visitor, thenBody);
		acceptVisitor(visitor, elseBody);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("if ");
		cp.append("(", conditionVar, ") ");
		cp.append(thenBody, " ");
		cp.append("else ", elseBody);
	}
	
}