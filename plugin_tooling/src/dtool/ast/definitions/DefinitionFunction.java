package dtool.ast.definitions;

import melnorme.lang.tooling.ast.IASTVisitor;
import melnorme.lang.tooling.ast.util.ASTCodePrinter;
import melnorme.lang.tooling.ast_actual.ASTNodeTypes;
import melnorme.utilbox.collections.ArrayView;
import dtool.ast.declarations.IDeclaration;
import dtool.ast.expressions.Expression;
import dtool.ast.references.Reference;
import dtool.ast.statements.IFunctionBody;
import dtool.ast.statements.IStatement;
import dtool.engine.common.IDeeNamedElement;
import dtool.engine.modules.IModuleResolver;
import dtool.parser.common.Token;
import dtool.resolver.CommonDefUnitSearch;

/**
 * A definition of a function.
 */
public class DefinitionFunction extends AbstractFunctionDefinition implements IDeclaration, IStatement{
	
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
	
	public IDeeNamedElement findReturnTypeTargetDefUnit(IModuleResolver moduleResolver) {
		if(retType == null) 
			return null;
		return retType.findTargetDefElement(moduleResolver);
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
	
	@Override
	public void resolveSearchInMembersScope(CommonDefUnitSearch search) {
		resolveSearchInMembersScopeForFunction(search, retType, tplParams, fnParams, tplConstraint);
	}
	
	@SuppressWarnings("unused")
	public static void resolveSearchInMembersScopeForFunction(CommonDefUnitSearch search, Reference retType,
		 ArrayView<TemplateParameter> tplParams, ArrayView<IFunctionParameter> fnParams, Expression tplConstraint) {
		// Do nothing, a function has no members scope
		// TODO: except for implicit function calls syntax, that needs to be implemented
	}
	
	@Override
	public IDeeNamedElement resolveTypeForValueContext(IModuleResolver mr) {
		// TODO implicit function call
		return null;
	}
	
}