package dtool.ast.statements;

import static dtool.util.NewUtils.assertNotNull_;


import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;

public class StatementIfVar extends Statement {
	
	public final SimpleVariableDef conditionVar;
	public final IStatement thenBody;
	public final IStatement elseBody;
	
	public StatementIfVar(SimpleVariableDef conditionVar, IStatement thenBody, IStatement elseBody) {
		this.conditionVar = parentize(assertNotNull_(conditionVar));
		this.thenBody = parentizeI(thenBody);
		this.elseBody = parentizeI(elseBody);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.STATEMENT_IF_VAR;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, conditionVar);
			TreeVisitor.acceptChildren(visitor, thenBody);
			TreeVisitor.acceptChildren(visitor, elseBody);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("if ");
		cp.append("(", conditionVar, ") ");
		cp.append(thenBody, " ");
		cp.append("else ", elseBody);
	}
	
}