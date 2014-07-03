package dtool.ast.definitions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.expressions.Expression;
import dtool.ast.references.Reference;
import dtool.resolver.CommonDefUnitSearch;

public class TemplateValueParam extends TemplateParameter {
	
	public final Reference type;
	public final Expression specializationValue;
	public final Expression defaultValue;
	
	public TemplateValueParam(Reference type, ProtoDefSymbol defId, Expression specializationValue, 
		Expression defaultValue) {
		super(defId);
		this.type = parentize(assertNotNull(type));
		this.specializationValue = parentize(specializationValue);
		this.defaultValue = parentize(defaultValue);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.TEMPLATE_VALUE_PARAM;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, type);
		acceptVisitor(visitor, defname);
		acceptVisitor(visitor, specializationValue);
		acceptVisitor(visitor, defaultValue);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(type, " ");
		cp.append(defname);
		cp.append(" : ", specializationValue);
		cp.append(" = ", defaultValue);
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Variable;
	}
	
	@Override
	public void resolveSearchInMembersScope(CommonDefUnitSearch search) {
		resolveSearchInReferredContainer(search, type);
	}
	
}