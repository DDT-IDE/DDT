package dtool.ast.references;

import java.util.Collection;

import descent.internal.compiler.parser.TypeReturn;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.definitions.DefUnit;

public class RefReturn extends Reference {
	
	
	public RefReturn(TypeReturn elem) {
		convertNode(elem);
	}
	
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
		}
		visitor.endVisit(this);
	}
	
	
	@Override
	public Collection<DefUnit> findTargetDefUnits(boolean findFirstOnly) {
		// TODO:
		return null;
	}
	
	
	@Override
	public String toStringAsElement() {
		return "return";
	}
	
	@Override
	public boolean canMatch(DefUnit defunit) {
		return false;
	}
	
}
