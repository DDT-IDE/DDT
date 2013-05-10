package dtool.ast.statements;

import static dtool.util.NewUtils.assertNotNull_;
import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.definitions.Symbol;

public class StatementGoto extends Statement {
	
	public final Symbol label;
	
	public StatementGoto(Symbol label) {
		this.label = parentize(assertNotNull_(label));
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.STATEMENT_GOTO;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, label);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("goto ");
		cp.append(label);
		cp.append(";");
	}
	
}