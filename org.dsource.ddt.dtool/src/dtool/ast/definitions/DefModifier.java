package dtool.ast.definitions;

import descent.internal.compiler.parser.TOK;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTVisitor;

@Deprecated
public class DefModifier extends ASTNeoNode {
	
	public final TOK tok;
	
	public DefModifier(TOK tok) {
		this.tok = tok;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
}