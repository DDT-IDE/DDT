package dtool.ast.definitions;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.references.Reference;
import dtool.refmodel.IScopeNode;

public class TemplateParamType extends TemplateParameter {
	
	public final Reference specType;
	public final Reference defaultType;
	
	public TemplateParamType(DefUnitDataTuple dudt, Reference specType, Reference defaultType){
		super(dudt);
		this.specType = parentize(specType);
		this.defaultType = parentize(defaultType);
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.TypeParameter;
	}
	
	/*
	 * Can be null
	 */
	@Override
	public IScopeNode getMembersScope() {
		if(specType == null)
			return null;
		return specType.getTargetScope();
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, defname);
			TreeVisitor.acceptChildren(visitor, specType);
			TreeVisitor.acceptChildren(visitor, defaultType);
		}
		visitor.endVisit(this);
	}
	
}