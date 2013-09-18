package dtool.ast.references;

import dtool.ast.expressions.Resolvable;
import dtool.resolver.api.DefUnitDescriptor;

/**
 * Common class for entity references.
 */
public abstract class Reference extends Resolvable {
	
	public abstract boolean canMatch(DefUnitDescriptor defunit);
	
}