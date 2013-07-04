package dtool.ast.definitions;

import java.util.Collection;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.expressions.IInitializer;
import dtool.resolver.IDefUnitReference;
import dtool.resolver.IScopeNode;
import dtool.resolver.api.IModuleResolver;

/**
 * A fragment of a variable definition in a multi-identifier variable declaration
 */
public class DefVarFragment extends DefUnit {
	
	public final IInitializer init;
	
	public DefVarFragment(ProtoDefSymbol defId, IInitializer init) {
		super(defId);
		this.init = parentize(init);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.DEFINITION_VAR_FRAGMENT;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, defname);
		acceptVisitor(visitor, init);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(defname);
		cp.append("= ", init);
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Variable;
	}
	
	public IInitializer getInitializer() {
		return init;
	}
	
	public IDefUnitReference getTypeReference() {
		return ((DefinitionVariable) getParent()).getTypeReference();
	}
	
	@Override
	public IScopeNode getMembersScope(IModuleResolver moduleResolver) {
		Collection<DefUnit> defunits = getTypeReference().findTargetDefUnits(moduleResolver, true);
		if(defunits == null || defunits.isEmpty())
			return null;
		return defunits.iterator().next().getMembersScope(moduleResolver);
		//return defunit.getMembersScope();
	}
	
	@Deprecated
	private String getTypeString() {
		if(getTypeReference() != null)
			return getTypeReference().toStringAsElement();
		return "auto";
	}
	
	@Override
	public String toStringForHoverSignature() {
		String str = getTypeString() + " " + getName();
		return str;
	}
	
	@Override
	public String toStringForCodeCompletion() {
		return defname.toStringAsCode() + "   " + getTypeString() + " - " + getModuleScope().toStringAsElement();
	}
	
}