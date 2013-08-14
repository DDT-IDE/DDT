package dtool.ast.references;

import java.util.Collection;

import melnorme.utilbox.core.CoreUtil;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.DefinitionFunction;
import dtool.ast.definitions.FunctionAttributes;
import dtool.ast.definitions.IFunctionParameter;
import dtool.ast.definitions.NativeDefUnit;
import dtool.resolver.CommonDefUnitSearch;
import dtool.resolver.DefUnitSearch;
import dtool.resolver.api.IModuleResolver;
import dtool.util.ArrayView;

/**
 * A function pointer type
 */
public class RefTypeFunction extends CommonRefNative {
	
	public final Reference retType;
	public final boolean isDelegate;
	public final ArrayView<IFunctionParameter> params;
	public final ArrayView<FunctionAttributes> fnAttributes;
	
	public RefTypeFunction(Reference retType, boolean isDelegate, ArrayView<IFunctionParameter> params, 
		ArrayView<FunctionAttributes> fnAttributes) {
		this.retType = parentize(retType);
		this.isDelegate = isDelegate;
		this.params = parentizeI(params);
		this.fnAttributes = fnAttributes;
	}
	
	public final ArrayView<ASTNode> getParams_asNodes() {
		return CoreUtil.blindCast(params);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.REF_TYPE_FUNCTION;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, retType);
		acceptVisitor(visitor, params);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(retType, " ");
		cp.append(isDelegate ? "delegate" : "function");
		cp.appendList("(", getParams_asNodes(), ",", ") ");
		cp.appendTokenList(fnAttributes, " ", true);
	}
	
	@Override
	public Collection<DefUnit> findTargetDefUnits(IModuleResolver moduleResolver, boolean findFirstOnly) {
		return DefUnitSearch.wrapResult(IntrinsicFunction.instance);
	}
	
	@Override
	public String toStringAsElement() {
		return Reference.maybeNullReference(retType).toStringAsElement() 
				+ " function"  
				+ DefinitionFunction.toStringParametersForSignature(params/*, varargs*/);
	}
	
	
	public static class IntrinsicFunction extends NativeDefUnit {
		public IntrinsicFunction() {
			super("<funtion>");
		}
		
		public static final IntrinsicFunction instance = new IntrinsicFunction();
		
		@Override
		public void resolveSearchInMembersScope(CommonDefUnitSearch search) {
			// TODO
		}
	}
}