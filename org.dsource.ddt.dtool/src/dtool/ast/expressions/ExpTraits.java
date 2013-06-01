package dtool.ast.expressions;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.NodeListView;
import dtool.ast.definitions.Symbol;

public class ExpTraits extends Expression {
	
	public final Symbol traitsId;
	public final NodeListView<Resolvable> args;
	
	public ExpTraits(Symbol traitsId, NodeListView<Resolvable> args) {
		this.traitsId = parentize(traitsId);
		this.args = parentize(args);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.EXP_TRAITS;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, traitsId);
			TreeVisitor.acceptChildren(visitor, args);
		}
		visitor.endVisit(this);	 
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("__traits");
		if(traitsId != null) {
			cp.append("(", traitsId);
			cp.appendNodeList(", ", args, ",", "");
			cp.append(")");
		}
	}
	
}