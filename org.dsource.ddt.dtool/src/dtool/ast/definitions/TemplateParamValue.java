package dtool.ast.definitions;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.expressions.Resolvable;
import dtool.ast.references.Reference;
import dtool.refmodel.IScopeNode;

public class TemplateParamValue extends TemplateParameter {

	public final Reference type;
	public final Resolvable specvalue;
	public final Resolvable defaultvalue;

	public TemplateParamValue(DefUnitDataTuple dudt, Reference type, Resolvable specValue, Resolvable defaultValue) {
		super(dudt);
		this.type = type; parentize(this.type);
		this.specvalue = specValue; parentize(this.specvalue);
		this.defaultvalue = defaultValue; parentize(this.defaultvalue);
	}

	@Override
	public EArcheType getArcheType() {
		return EArcheType.Variable;
	}

	@Override
	public IScopeNode getMembersScope() {
		return type.getTargetScope();
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, type);
			TreeVisitor.acceptChildren(visitor, defname);
			TreeVisitor.acceptChildren(visitor, specvalue);
			TreeVisitor.acceptChildren(visitor, defaultvalue);
		}
		visitor.endVisit(this);	
	}
	
}
