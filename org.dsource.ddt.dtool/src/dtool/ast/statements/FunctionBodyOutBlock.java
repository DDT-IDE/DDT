package dtool.ast.statements;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.definitions.Symbol;

public class FunctionBodyOutBlock extends ASTNode {
	
	public final Symbol result; // TODO convert this to DefUnit
	public final BlockStatement block;
	
	public FunctionBodyOutBlock(Symbol result, BlockStatement block) {
		this.result = parentize(result);
		this.block = parentize(block);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.FUNCTION_BODY_OUT_BLOCK;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		if (visitor.visit(this)) {
			TreeVisitor.acceptChildren(visitor, result);
			TreeVisitor.acceptChildren(visitor, block);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("out");
		cp.append("(", result, ")");
		cp.append(block);
	}
	
}