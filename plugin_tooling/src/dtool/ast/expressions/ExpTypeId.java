package dtool.ast.expressions;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.references.Reference;

public class ExpTypeId extends Expression {
	
	public final Reference typeArgument;
	public final Expression expressionArgument;
	
	public ExpTypeId(Reference typeArgument) {
		this.typeArgument = parentize(typeArgument);
		this.expressionArgument = null;
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.EXP_TYPEID;
	}
	
	public ExpTypeId(Expression expressionArgument) {
		this.typeArgument = null;
		this.expressionArgument = parentize(expressionArgument);
	}
	
	public Resolvable getArgument() {
		return typeArgument != null ? typeArgument : expressionArgument;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, getArgument());
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("typeid");
		cp.append("(", getArgument(), ")");
	}
	
}