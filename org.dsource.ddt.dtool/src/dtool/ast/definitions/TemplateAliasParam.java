package dtool.ast.definitions;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.expressions.Resolvable;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.pluginadapters.IModuleResolver;

public class TemplateAliasParam extends TemplateParameter {
	
	public final Resolvable specializationValue;
	public final Resolvable defaultValue;
	
	public TemplateAliasParam(DefUnitTuple dudt, Resolvable specializationValue, Resolvable defaultValue){
		super(dudt);
		this.specializationValue = parentize(specializationValue);
		this.defaultValue = parentize(defaultValue);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.TEMPLATE_ALIAS_PARAM;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, defname);
			TreeVisitor.acceptChildren(visitor, specializationValue);
			TreeVisitor.acceptChildren(visitor, defaultValue);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("alias ");
		cp.appendNode(defname);
		cp.appendNode(" : ", specializationValue);
		cp.appendNode(" = ", defaultValue);
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Alias;
	}
	
	@Override
	public IScopeNode getMembersScope(IModuleResolver moduleResolver) {
		// TODO return intrinsic universal
		return null;
	}

}