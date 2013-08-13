package dtool.ast.definitions;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.declarations.IDeclaration;
import dtool.ast.expressions.Expression;
import dtool.ast.references.Reference;
import dtool.ast.statements.IFunctionBody;
import dtool.ast.statements.IStatement;
import dtool.parser.Token;
import dtool.resolver.api.IModuleResolver;
import dtool.util.ArrayView;

/**
 * A definition of a function.
 */
public class DefinitionFunction extends AbstractFunctionDefinition implements IDeclaration, IStatement {
	
	public final Reference retType;
	
	public DefinitionFunction(Token[] comments, Reference retType, ProtoDefSymbol defId,
		ArrayView<TemplateParameter> tplParams, ArrayView<IFunctionParameter> fnParams,
		ArrayView<FunctionAttributes> fnAttributes, Expression tplConstraint, IFunctionBody fnBody)
	{
		super(comments, defId, tplParams, fnParams, fnAttributes, tplConstraint, fnBody);
		this.retType = parentize(retType);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DEFINITION_FUNCTION;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, retType);
		acceptVisitor(visitor, defname);
		acceptVisitor(visitor, tplParams);
		acceptVisitor(visitor, fnParams);
		acceptVisitor(visitor, tplConstraint);
		acceptVisitor(visitor, fnBody);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(retType, " ");
		toStringAsCode_fromDefId(cp);
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Function;
	}
	
	public DefUnit findReturnTypeTargetDefUnit(IModuleResolver moduleResolver) {
		if(retType == null) 
			return null;
		return retType.findTargetDefUnit(moduleResolver);
	}
	
	@Override
	public String toStringAsElement() {
		return getName() + toStringParametersForSignature();
	}
	
	@Override
	public String getExtendedName() {
		return getName() + toStringParametersForSignature();
	}
	
	public String toStringParametersForSignature() {
		return toStringParametersForSignature(fnParams);
	}
	
	public static String toStringParametersForSignature(ArrayView<IFunctionParameter> params) {
		if(params == null) 
			return "";
		String strParams = "(";
		for (int i = 0; i < params.size(); i++) {
			if(i != 0)
				strParams += ", ";
			strParams += params.get(i).toStringForFunctionSignature();
		}
		return strParams + ")";
	}
	
}