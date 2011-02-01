package dtool.ast.definitions;

import descent.internal.compiler.parser.Modifier;
import descent.internal.compiler.parser.TOK;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;

public class DefModifier extends ASTNeoNode {

	public TOK tok;
	
	public DefModifier(Modifier node) {
		convertNode(node);
		this.tok = node.tok;
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

}
