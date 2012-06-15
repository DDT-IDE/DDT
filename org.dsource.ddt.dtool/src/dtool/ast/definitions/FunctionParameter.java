package dtool.ast.definitions;

import java.util.Collection;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.NodeUtil;
import dtool.ast.expressions.Resolvable;
import dtool.ast.references.Reference;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.pluginadapters.IModuleResolver;

// TODO: Need to test this a lot more, especially with many other kinds of parameters
public class FunctionParameter extends DefUnit implements IFunctionParameter {
	
	public final Reference type;
	public final int storageClass;
	public final Resolvable defaultValue;
	
	public FunctionParameter(DefUnitDataTuple dudt, int storageClass, Reference type, Resolvable defaultValue) {
		super(dudt);
		// assertNotNull(this.type);
		this.storageClass = storageClass;
		this.type = parentize(type);
		this.defaultValue = parentize(defaultValue);
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Variable;
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
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