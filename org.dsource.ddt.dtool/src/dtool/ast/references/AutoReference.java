package dtool.ast.references;

import java.util.Collection;

import dtool.ast.ASTCodePrinter;
import dtool.ast.ASTNodeTypes;
import dtool.ast.IASTVisitor;
import dtool.ast.definitions.DefUnit;
import dtool.refmodel.api.DefUnitDescriptor;
import dtool.refmodel.api.IModuleResolver;

/**
 * This reference node can only be parsed in special circumstances
 */
public final class AutoReference extends Reference {
	
	public AutoReference() {}
	
	@Override
	public ASTNodeTypes getNodeType() {
		return ASTNodeTypes.REF_AUTO;
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
	@Override
	public void toStringAsCode(ASTCodePrinter cp) {
		cp.append("auto");
	}
	
	@Override
	public Collection<DefUnit> findTargetDefUnits(IModuleResolver moduleResolver, boolean findFirstOnly) {
		return null;
	}
	
	@Override
	public boolean canMatch(DefUnitDescriptor defunit) {
		return false;
	}
}