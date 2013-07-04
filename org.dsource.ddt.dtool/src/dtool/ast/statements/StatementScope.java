package dtool.ast.statements;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.definitions.Symbol;

public class StatementScope extends Statement {
	
	public enum ScopeTypes {
		ON_EXIT,
		ON_SUCCESS,
		ON_FAILURE,
		;
		
		public static ScopeTypes fromIdentifier(String scopeId) {
			if("exit".equals(scopeId)) return ON_EXIT;
			if("success".equals(scopeId)) return ON_SUCCESS;
			if("failure".equals(scopeId)) return ON_FAILURE;
			return null;
		}
	}
	
	public final Symbol scopeTypeId;
	public final IStatement body;
	
	public StatementScope(Symbol scopeTypeId, IStatement body) {
		this.scopeTypeId = parentize(scopeTypeId);
		this.body = parentizeI(body);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.STATEMENT_SCOPE;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, scopeTypeId);
		acceptVisitor(visitor, body);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("scope ");
		cp.append("(", scopeTypeId, ")");
		cp.append(body);
	}
	
}