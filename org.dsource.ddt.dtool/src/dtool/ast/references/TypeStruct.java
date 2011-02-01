package dtool.ast.references;

import java.util.Collection;

import dtool.ast.IASTNeoVisitor;
import dtool.ast.definitions.DefUnit;

/**
 */
public class TypeStruct extends CommonRefNative {

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<DefUnit> findTargetDefUnits(boolean findFirstOnly) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String toStringAsElement() {
		throw new UnsupportedOperationException();
	}

}
