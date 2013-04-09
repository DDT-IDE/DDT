package dtool.ast.definitions;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.references.Reference;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.pluginadapters.IModuleResolver;

public class TemplateTypeParam extends TemplateParameter {
	
	public final Reference specializationType;
	public final Reference defaultType;
	
	public TemplateTypeParam(DefUnitTuple dudt, Reference specializationType, Reference defaultType){
		super(dudt);
		this.specializationType = parentize(specializationType);
		this.defaultType = parentize(defaultType);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.TEMPLATE_TYPE_PARAM;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, defname);
			TreeVisitor.acceptChildren(visitor, specializationType);
			TreeVisitor.acceptChildren(visitor, defaultType);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.appendNode(defname);
		cp.appendNode(" : ", specializationType);
		cp.appendNode(" = ", defaultType);
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
		if(specializationType == null)
			return null;
		return specializationType.getTargetScope(moduleResolver);
	}

	
}