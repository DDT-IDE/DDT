package dtool.ast.statements;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.Collection;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.EArcheType;
import dtool.ast.expressions.Expression;
import dtool.ast.references.Reference;
import dtool.resolver.IScopeNode;
import dtool.resolver.api.IModuleResolver;

public class VariableDefWithInit extends DefUnit {
	
	public final Reference type;
	public final Expression defaultValue;
	
	public VariableDefWithInit(Reference type, ProtoDefSymbol defId, Expression defaultValue) {
		super(defId);
		this.type = parentize(assertNotNull(type));
		this.defaultValue = parentize(defaultValue);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.VARIABLE_DEF_WITH_INIT;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, type);
			TreeVisitor.acceptChildren(visitor, defname);
			TreeVisitor.acceptChildren(visitor, defaultValue);
		}
		visitor.endVisit(this);	
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(type, " ");
		cp.append(defname);
		cp.append(" = ", defaultValue);
	}
	
	@Override
	public EArcheType getArcheType() {
		return EArcheType.Variable;
	}
	
	@Override
	public IScopeNode getMembersScope(IModuleResolver moduleResolver) {
		Collection<DefUnit> defunits = type.findTargetDefUnits(moduleResolver, true);
		if(defunits == null || defunits.isEmpty())
			return null;
		return defunits.iterator().next().getMembersScope(moduleResolver);
		//return defunit.getMembersScope();
	}
	
}