package dtool.ast.definitions;

import static dtool.util.NewUtils.assertNotNull_;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.Collection;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.NodeUtil;
import dtool.ast.SourceRange;
import dtool.ast.expressions.Expression;
import dtool.ast.references.Reference;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.pluginadapters.IModuleResolver;
import dtool.util.ArrayView;

public class FunctionParameter extends DefUnit implements IFunctionParameter {
	
	public final FnParameterAttributes paramAttribs;
	public final Reference type;
	public final Expression defaultValue;
	public final boolean isVariadic;
	
	public FunctionParameter(ArrayView<FunctionParamAttribKinds> attribList, Reference type, DefUnitTuple dudt, 
		Expression defaultValue, boolean isVariadic, SourceRange sourceRange) {
		super(dudt);
		this.paramAttribs = FnParameterAttributes.create(attribList);
		this.type = parentize(assertNotNull_(type));
		this.defaultValue = parentize(defaultValue);
		assertTrue(!isVariadic || defaultValue == null);
		this.isVariadic = isVariadic;
		initSourceRange(sourceRange);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.FUNCTION_PARAMETER;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, type);
			//TreeVisitor.acceptChildren(visitor, inout);
			TreeVisitor.acceptChildren(visitor, defname);
			TreeVisitor.acceptChildren(visitor, defaultValue);
		}
		visitor.endVisit(this);	
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		paramAttribs.toStringAsCode(cp);
		cp.appendNode(type, " ");
		cp.appendNode(defname);
		cp.appendNode(" = ", defaultValue);
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
	public IScopeNode getMembersScope(IModuleResolver moduleResolver) {
		Collection<DefUnit> defunits = type.findTargetDefUnits(moduleResolver, true);
		if(defunits == null || defunits.isEmpty())
			return null;
		return defunits.iterator().next().getMembersScope(moduleResolver);
		//return defunit.getMembersScope();
	}
	
	@Override
	public String toStringForHoverSignature() {
		return type.toStringAsElement() + " " + getName();
	}
	
	@Override
	public String toStringForCodeCompletion() {
		return getName() + "   " + type.toStringAsElement() + " - "
				+ NodeUtil.getOuterDefUnit(this).toStringAsElement();
	}
	
	@Override
	public String toStringAsFunctionSignaturePart() {
		return type.toStringAsElement() + " " + getName();
	}
	
	@Override
	public String toStringAsFunctionSimpleSignaturePart() {
		return type.toStringAsElement();
	}
	
	@Override
	public String toStringInitializer() {
		if(defaultValue == null)
			return null;
		return defaultValue.toStringAsElement();
	}
	
}