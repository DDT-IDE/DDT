package dtool.ast.definitions;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTNeoVisitor;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.pluginadapters.IModuleResolver;

public class TemplateParamTuple extends TemplateParameter {
	
	public TemplateParamTuple(DefUnitTuple dudt) {
		super(dudt);
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Tuple;
	}
	
	@Override
	public IScopeNode getMembersScope(IModuleResolver moduleResolver) {
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