package dtool.ast.statements;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.parser.IToken;
import dtool.util.ArrayView;

public class StatementAsm extends Statement {
	
	public final ArrayView<IToken> tokens;
	
	public StatementAsm(ArrayView<IToken> tokens) {
		this.tokens = tokens;
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.STATEMENT_ASM;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("asm ");
		if(tokens != null) {
			cp.append("{");
			cp.appendTokenList(tokens, " ", true);
			cp.append("}");
		}
	}
	
}