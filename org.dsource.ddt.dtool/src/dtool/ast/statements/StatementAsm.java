package dtool.ast.statements;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.parser.Token;
import dtool.util.ArrayView;

public class StatementAsm extends Statement {
	
	public final ArrayView<Token> tokens;
	
	public StatementAsm(ArrayView<Token> tokens) {
		this.tokens = tokens;
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.STATEMENT_ASM;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
		}
		visitor.endVisit(this);
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