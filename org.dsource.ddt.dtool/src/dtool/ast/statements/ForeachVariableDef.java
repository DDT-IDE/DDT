package dtool.ast.statements;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.EArcheType;
import dtool.ast.references.Reference;
import dtool.resolver.CommonDefUnitSearch;

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
	public void visitChildren(IASTVisitor visitor) {
		acceptVisitor(visitor, type);
		acceptVisitor(visitor, defname);
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
	public void resolveSearchInMembersScope(CommonDefUnitSearch search) {
		Reference.resolveSearchInReferedMembersScope(search, type);
	}
	
}