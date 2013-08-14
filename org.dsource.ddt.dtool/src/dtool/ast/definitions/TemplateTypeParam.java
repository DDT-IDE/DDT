package dtool.ast.definitions;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.references.Reference;
import dtool.resolver.CommonDefUnitSearch;

public class TemplateTypeParam extends TemplateParameter {
	
	public final Reference specializationType;
	public final Reference defaultType;
	
	public TemplateTypeParam(ProtoDefSymbol defId, Reference specializationType, Reference defaultType){
		super(defId);
		this.specializationType = parentize(specializationType);
		this.defaultType = parentize(defaultType);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.TEMPLATE_TYPE_PARAM;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, defname);
		acceptVisitor(visitor, specializationType);
		acceptVisitor(visitor, defaultType);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(defname);
		cp.append(" : ", specializationType);
		cp.append(" = ", defaultType);
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.TypeParameter;
	}
	
	@Override
	public void resolveSearchInMembersScope(CommonDefUnitSearch search) {
		Reference.resolveSearchInReferedMembersScope(search, specializationType);
	}
	
}