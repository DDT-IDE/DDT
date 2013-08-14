package dtool.ast.definitions;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.resolver.CommonDefUnitSearch;

public class TemplateTupleParam extends TemplateParameter {
	
	public TemplateTupleParam(ProtoDefSymbol defId) {
		super(defId);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.TEMPLATE_TUPLE_PARAM;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, defname);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(defname);
		cp.append("...");
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Tuple;
	}
	
	@Override
	public void resolveSearchInMembersScope(CommonDefUnitSearch search) {
		// TODO return intrinsic universal
		return;
	}
	
}