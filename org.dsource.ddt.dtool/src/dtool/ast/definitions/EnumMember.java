package dtool.ast.definitions;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.expressions.Resolvable;
import dtool.ast.references.Reference;
import dtool.refmodel.IScopeNode;

public class EnumMember extends DefUnit {
	
	public final Resolvable value;
	
	public EnumMember(DefUnitDataTuple defunit, Resolvable value) {
		super(defunit);
		this.value = parentize(value);
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, defname);
			TreeVisitor.acceptChildren(visitor, value);
		}
		visitor.endVisit(this);	 			
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.EnumMember;
	}
	
	@Override
	public IScopeNode getMembersScope() {
		return getType().getTargetScope();
	}
	
	private Reference getType() {
		return ((DefinitionEnum) getParent()).type;
	}
	
}