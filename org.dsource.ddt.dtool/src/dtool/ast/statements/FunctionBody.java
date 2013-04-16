package dtool.ast.statements;

import static dtool.util.NewUtils.assertNotNull_;
import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNeoNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;

public class FunctionBody extends ASTNeoNode implements IFunctionBody {
	
	public final BlockStatement bodyBlock;
	
	public FunctionBody(BlockStatement bodyBlock) {
		this.bodyBlock = parentize(assertNotNull_(bodyBlock));
	}
	
	protected FunctionBody(BlockStatement bodyBlock, @SuppressWarnings("unused") boolean dummy) {
		this.bodyBlock = parentize(bodyBlock);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.FUNCTION_BODY;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		if (visitor.visit(this)) {
			TreeVisitor.acceptChildren(visitor, bodyBlock);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.appendNode("body", bodyBlock);
	}
	
}