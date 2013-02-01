package dtool.ast.expressions;

import static dtool.util.NewUtils.assertNotNull_;
import dtool.ast.ASTCodePrinter;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.parser.Token;

public class ExpLiteralInteger extends Expression {
	
	public final Token num;
	
	public ExpLiteralInteger(Token num, SourceRange sourceRange) {
		this.num = assertNotNull_(num);
		initSourceRange(sourceRange);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);	 
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(num);
	}
	
}