package dtool.ast.expressions;

import melnorme.utilbox.core.CoreUtil;
import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNeoNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.definitions.FunctionAttributes;
import dtool.ast.definitions.IFunctionParameter;
import dtool.ast.references.Reference;
import dtool.ast.statements.IFunctionBody;
import dtool.util.ArrayView;

public class ExpFunctionLiteral extends Expression {
	
	public final Boolean isFunctionKeyword;
	public final Reference retType;
	public final ArrayView<IFunctionParameter> fnParams;
	public final ArrayView<FunctionAttributes> fnAttributes;
	public final IFunctionBody fnBody;
	
	
	public ExpFunctionLiteral(Boolean isFunctionKeyword, Reference retType, ArrayView<IFunctionParameter> fnParams,
		ArrayView<FunctionAttributes> fnAttributes, IFunctionBody fnBody) {
		this.isFunctionKeyword = isFunctionKeyword;
		this.retType = parentize(retType);
		this.fnParams = parentizeI(fnParams);
		this.fnAttributes = fnAttributes;
		this.fnBody = parentizeI(fnBody);
	}
	
	public final ArrayView<ASTNeoNode> getParams_asNodes() {
		return CoreUtil.blindCast(fnParams);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.EXP_FUNCTION_LITERAL;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, retType);
			TreeVisitor.acceptChildren(visitor, fnParams);
			TreeVisitor.acceptChildren(visitor, fnBody);
		}
		visitor.endVisit(this);	 
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(isFunctionKeyword == Boolean.TRUE, "function ");
		cp.append(isFunctionKeyword == Boolean.FALSE, "delegate ");
		cp.appendNode(retType);
		cp.appendNodeList("(", getParams_asNodes(), ",", ") ");
		cp.appendList(fnAttributes, " ", true);
		cp.appendNode(fnBody);
	}
	
}