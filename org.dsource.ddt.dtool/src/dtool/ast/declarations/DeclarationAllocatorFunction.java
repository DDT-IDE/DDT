package dtool.ast.declarations;

import melnorme.utilbox.core.CoreUtil;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
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
public class DeclarationAllocatorFunction extends ASTNode implements IDeclaration {
	
	public final boolean isNew;
	public final ArrayView<IFunctionParameter> params;
	public final IFunctionBody fnBody;
	
	public DeclarationAllocatorFunction(boolean isNew, ArrayView<IFunctionParameter> params, IFunctionBody fnBody) {
		this.isNew = isNew;
		this.params = parentizeI(params);
		this.fnBody = parentizeI(fnBody);
	}
	
	public final ArrayView<ASTNode> getParams_asNodes() {
		return CoreUtil.blindCast(params);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DECLARATION_ALLOCATOR_FUNCTION;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, params);
		acceptVisitor(visitor, fnBody);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(isNew ? "new" : "delete");
		cp.appendList("(", getParams_asNodes(), ",", ") ");
		cp.append(fnBody);
	}
	
}