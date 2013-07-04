package dtool.ast.expressions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.utilbox.core.CoreUtil;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
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
	public final Expression bodyExpression;
	
	public ExpFunctionLiteral(Boolean isFunctionKeyword, Reference retType, ArrayView<IFunctionParameter> fnParams,
		ArrayView<FunctionAttributes> fnAttributes, IFunctionBody fnBody, Expression bodyExpression) {
		this.isFunctionKeyword = isFunctionKeyword;
		this.retType = parentize(retType);
		this.fnParams = parentizeI(fnParams);
		this.fnAttributes = fnAttributes;
		this.fnBody = parentize(fnBody);
		this.bodyExpression = parentize(bodyExpression);
		assertTrue(fnBody == null || bodyExpression == null); // only one of each
	}
	
	public final ArrayView<ASTNode> getParams_asNodes() {
		return CoreUtil.blindCast(fnParams);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.EXP_FUNCTION_LITERAL;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, retType);
		acceptVisitor(visitor, fnParams);
		acceptVisitor(visitor, fnBody);
		acceptVisitor(visitor, bodyExpression);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(isFunctionKeyword == Boolean.TRUE, "function ");
		cp.append(isFunctionKeyword == Boolean.FALSE, "delegate ");
		cp.append(retType);
		cp.appendList("(", getParams_asNodes(), ",", ") ");
		cp.appendTokenList(fnAttributes, " ", true);
		cp.append(fnBody);
		cp.append(" => ", bodyExpression);
	}
	
}