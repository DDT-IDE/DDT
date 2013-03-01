package dtool.ast.expressions;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.SourceRange;

public class ExpImportString extends Expression {
	
	final public Expression exp; 
	
	public ExpImportString(Expression exp, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.exp = parentize(exp);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.EXP_IMPORT_STRING;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, exp);
		}
		visitor.endVisit(this);	 
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("import");
		cp.appendNode("(", exp, ")");
	}
}