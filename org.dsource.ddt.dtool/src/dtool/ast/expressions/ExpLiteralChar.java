package dtool.ast.expressions;

import static dtool.util.NewUtils.assertNotNull_;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.parser.Token;

public class ExpLiteralChar extends Expression {
	
	public final Token ch;
	
	public ExpLiteralChar(Token num) {
		this.ch = assertNotNull_(num);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.EXP_LITERAL_CHAR;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);	 
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(ch);
	}
	
}