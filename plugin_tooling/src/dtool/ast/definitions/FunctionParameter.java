package dtool.ast.definitions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.expressions.Expression;
import dtool.ast.references.Reference;
import dtool.engine.common.DefElementCommon;
import dtool.engine.modules.IModuleResolver;
import dtool.parser.LexElement;
import dtool.resolver.CommonDefUnitSearch;
import dtool.util.ArrayView;

public class FunctionParameter extends DefUnit implements IFunctionParameter {
	
	public final FnParameterAttributes paramAttribs;
	public final Reference type;
	public final Expression defaultValue;
	public final boolean isVariadic;
	
	public FunctionParameter(ArrayView<LexElement> attribList, Reference type, ProtoDefSymbol defId, 
		Expression defaultValue, boolean isVariadic) {
		super(defId);
		this.paramAttribs = FnParameterAttributes.create(attribList);
		this.type = parentize(assertNotNull(type));
		this.defaultValue = parentize(defaultValue);
		assertTrue(!isVariadic || defaultValue == null);
		this.isVariadic = isVariadic;
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.FUNCTION_PARAMETER;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, type);
		acceptVisitor(visitor, defname);
		acceptVisitor(visitor, defaultValue);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		paramAttribs.toStringAsCode(cp);
		cp.append(type, " ");
		cp.append(defname);
		cp.append(" = ", defaultValue);
		cp.append(isVariadic, "...");
	}
	
	@Override
	public boolean isVariadic() {
		return isVariadic;
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Variable;
	}
	
	@Override
	public String getTypeStringRepresentation() {
		return getStringRepresentation(type, null, isVariadic);
	}
	
	@Override
	public String getInitializerStringRepresentation() {
		if(defaultValue == null)
			return null;
		return defaultValue.toStringAsCode();
	}
	
	@Override
	public String toStringForFunctionSignature() {
		return getStringRepresentation(type, getName(), isVariadic);
	}
	
	public static String getStringRepresentation(Reference type, String name, boolean isVariadic) {
		String nameStr = name == null ? "": " " + name;
		return type.toStringAsCode() + nameStr + (isVariadic ? "..." : "");
	}
	
	@Override
	public void resolveSearchInMembersScope(CommonDefUnitSearch search) {
		resolveSearchInReferredContainer(search, type);
	}
	
	@Override
	public INamedElement resolveTypeForValueContext(IModuleResolver mr) {
		return DefElementCommon.resolveTypeForValueContext(mr, type);
	}
	
}