package dtool.ast.definitions;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.expressions.Resolvable;
import dtool.ast.references.Reference;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.pluginadapters.IModuleResolver;

public class TemplateParamValue extends TemplateParameter {
	
	public final Reference type;
	public final Resolvable specValue;
	public final Resolvable defaultValue;
	
	public TemplateParamValue(DefUnitTuple dudt, Reference type, Resolvable specValue, Resolvable defaultValue) {
		super(dudt);
		this.type = parentize(type);
		this.specValue = parentize(specValue);
		this.defaultValue = parentize(defaultValue);
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Variable;
	}
	
	@Override
	public IScopeNode getMembersScope(IModuleResolver moduleResolver) {
		return type.getTargetScope(moduleResolver);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, type);
			TreeVisitor.acceptChildren(visitor, defname);
			TreeVisitor.acceptChildren(visitor, specValue);
			TreeVisitor.acceptChildren(visitor, defaultValue);
		}
		visitor.endVisit(this);	
	}
	
}