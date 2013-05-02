package dtool.ast.declarations;

import melnorme.utilbox.core.CoreUtil;
import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNeoNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.definitions.IFunctionParameter;
import dtool.ast.statements.IFunctionBody;
import dtool.util.ArrayView;

/**
 * Declaration of special function like elements, like allocator/deallocator:
 * http://dlang.org/class.html#ClassAllocator
 * http://dlang.org/class.html#ClassDeallocator
 */
public class DeclarationSpecialFunction extends ASTNeoNode {
	
	public final boolean isNew;
	public final ArrayView<IFunctionParameter> params;
	public final IFunctionBody fnBody;
	
	public DeclarationSpecialFunction(boolean isNew, ArrayView<IFunctionParameter> params, IFunctionBody fnBody) {
		this.isNew = isNew;
		this.params = parentizeI(params);
		this.fnBody = parentizeI(fnBody);
	}
	
	public final ArrayView<ASTNeoNode> getParams_asNodes() {
		return CoreUtil.blindCast(params);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DECLARATION_ALLOC_DEALLOC;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, params);
			TreeVisitor.acceptChildren(visitor, fnBody);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(isNew ? "new" : "delete");
		cp.appendNodeList("(", getParams_asNodes(), ",", ") ");
		cp.appendNode(fnBody);
	}
	
}