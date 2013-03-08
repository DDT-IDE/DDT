package dtool.ast.statements;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.SourceRange;

public class InOutFunctionBody extends FunctionBody implements IFunctionBody {
	
	public final boolean isOutIn; // Indicates if 'out' block appears before 'in' block in the source
	public final BlockStatement inBlock;
	public final FunctionBodyOutBlock outBlock;
	
	public InOutFunctionBody(boolean isOutIn, BlockStatement inBlock, FunctionBodyOutBlock outBlock, 
		BlockStatement bodyBlock, SourceRange sourceRange) {
		super(bodyBlock);
		this.isOutIn = isOutIn;
		this.inBlock = parentize(inBlock);
		this.outBlock = parentize(outBlock);
		initSourceRange(sourceRange);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.IN_OUT_FUNCTION_BODY;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		if (visitor.visit(this)) {
			TreeVisitor.acceptChildren(visitor, isOutIn ? outBlock : inBlock);
			TreeVisitor.acceptChildren(visitor, isOutIn ? inBlock : outBlock);
			TreeVisitor.acceptChildren(visitor, bodyBlock);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		if(isOutIn) {
			cp.appendNode(outBlock);
			cp.appendNode("in", inBlock);
		} else {
			cp.appendNode("in", inBlock);
			cp.appendNode(outBlock);
		}
		cp.appendNode("body", bodyBlock);
	}
	
}