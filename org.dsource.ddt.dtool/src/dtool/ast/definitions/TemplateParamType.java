package dtool.ast.definitions;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.IASTVisitor;
import dtool.ast.references.Reference;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.pluginadapters.IModuleResolver;

public class TemplateParamType extends TemplateParameter {
	
	public final Reference specType;
	public final Reference defaultType;
	
	public TemplateParamType(DefUnitTuple dudt, Reference specType, Reference defaultType){
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
	public IScopeNode getMembersScope(IModuleResolver moduleResolver) {
		if(specType == null)
			return null;
		return specType.getTargetScope(moduleResolver);
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, defname);
			TreeVisitor.acceptChildren(visitor, specType);
			TreeVisitor.acceptChildren(visitor, defaultType);
		}
		visitor.endVisit(this);
	}
	
}