package dtool.ast.expressions;

import static dtool.util.NewUtils.assertNotNull_;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.parser.Token;

public class ExpLiteralFloat extends Expression {
	
	public final Token floatNum;
	
	public ExpLiteralFloat(Token floatNum) {
		this.floatNum = assertNotNull_(floatNum);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.EXP_LITERAL_FLOAT;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);	 
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.appendToken(floatNum);
	}
	
}