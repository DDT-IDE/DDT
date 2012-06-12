package dtool.ast.definitions;

import descent.internal.compiler.parser.TOK;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;

public class DefModifier extends ASTNeoNode {
	
	public final TOK tok;
	
	public DefModifier(TOK tok, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.tok = tok;
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
}