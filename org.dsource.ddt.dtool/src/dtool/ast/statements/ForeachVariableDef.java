package dtool.ast.statements;

import java.util.Collection;

import melnorme.utilbox.tree.TreeVisitor;
import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.EArcheType;
import dtool.ast.references.Reference;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.api.IModuleResolver;

public class ForeachVariableDef extends DefUnit {
	
	public final boolean isRef;
	public final Reference type;
	
	public ForeachVariableDef(boolean isRef, Reference type, ProtoDefSymbol defId) {
		super(defId);
		this.isRef = isRef;
		this.type = parentize(type);
	}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.FOREACH_VARIABLE_DEF;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, type);
			TreeVisitor.acceptChildren(visitor, defname);
		}
		visitor.endVisit(this);	
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append(isRef, "ref ");
		cp.append(type, " ");
		cp.append(defname);
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