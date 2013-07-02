package dtool.ast.definitions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.expressions.Expression;
import dtool.ast.references.Reference;
import dtool.resolver.IScopeNode;
import dtool.resolver.api.IModuleResolver;

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
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, type);
			TreeVisitor.acceptChildren(visitor, defname);
			TreeVisitor.acceptChildren(visitor, specializationValue);
			TreeVisitor.acceptChildren(visitor, defaultValue);
		}
		visitor.endVisit(this);	
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
	public IScopeNode getMembersScope(IModuleResolver moduleResolver) {
		return type.getTargetScope(moduleResolver);
	}
	
}