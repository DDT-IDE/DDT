package dtool.ast.expressions;

import static dtool.util.NewUtils.assertNotNull_;
import melnorme.utilbox.core.CoreUtil;
import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNeoNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.definitions.IFunctionParameter;
import dtool.util.ArrayView;

/**
 * A lambda expression in the syntax:
 * <code> ParameterAttributes => AssignExpression </code>
 */
public class ExpLambda extends Expression {
	
	public final ArrayView<IFunctionParameter> params;
	public final Expression bodyExpression;
	
	public ExpLambda(ArrayView<IFunctionParameter> params, Expression bodyExpression) {
		this.params = assertNotNull_(parentizeI(params));
		this.bodyExpression = bodyExpression;
	}
	
	public final ArrayView<ASTNeoNode> getParams_asNodes() {
		return CoreUtil.blindCast(params);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.EXP_LAMBDA;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, params);
			TreeVisitor.acceptChildren(visitor, bodyExpression);
		}
		visitor.endVisit(this);	 
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.appendNodeList("(", getParams_asNodes(), ",", ") ");
		cp.append(" => ");
		cp.appendNode(bodyExpression, ";");
	}
	
}