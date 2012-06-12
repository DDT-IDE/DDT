package dtool.ast.definitions;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTNeoVisitor;
import dtool.refmodel.IScopeNode;

public class TemplateParamAlias extends TemplateParameter {
	
	public TemplateParamAlias(DefUnitDataTuple dudt) {
		super(dudt);
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Alias;
	}
	
	@Override
	public IScopeNode getMembersScope() {
		// TODO return intrinsic universal
		return null;
	}
	
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, defname);
		}
		visitor.endVisit(this);
	}
}