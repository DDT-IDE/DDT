package dtool.ast.expressions;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.SourceRange;
import dtool.ast.references.Reference;

public class ExpTypeId extends Expression {
	
	public final Reference typeArgument;
	public final Expression expressionArgument;
	
	public ExpTypeId(Reference typeArgument, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.typeArgument = parentize(typeArgument);
		this.expressionArgument = null;
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.EXP_TYPEID;
	}
	
	public ExpTypeId(Expression expressionArgument, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.typeArgument = null;
		this.expressionArgument = parentize(expressionArgument);
	}
	
	public Resolvable getArgument() {
		return typeArgument != null ? typeArgument : expressionArgument;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, getArgument());
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("typeid");
		cp.appendNode("(", getArgument(), ")");
	}
	
}