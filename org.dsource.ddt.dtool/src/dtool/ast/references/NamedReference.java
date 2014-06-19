package dtool.ast.references;

import java.util.Collection;

import dtool.ast.definitions.INamedElement;
import dtool.ast.expressions.Resolvable.IQualifierNode;
import dtool.engine.modules.IModuleResolver;
import dtool.resolver.DefUnitSearch;
import dtool.resolver.api.DefUnitDescriptor;

/** 
 * A reference based on an identifier. These references also 
 * allow doing a search based on their lookup rules.
 */
public abstract class NamedReference extends Reference implements IQualifierNode {
	
	/** @return the central/primary name of this reference. 
	 * (that usually means the rightmost identifier without qualifiers).
	 * Can be null. */
	public abstract String getCoreReferenceName();
	
	/** @return whether the core reference is missing or not (it can be missing on syntax errors). */
	public boolean isMissingCoreReference() {
		return getCoreReferenceName() == null || getCoreReferenceName().isEmpty();
	}
	
	
	@Override
	public Collection<INamedElement> findTargetDefElements(IModuleResolver moduleResolver, boolean findOneOnly) {
		if(isMissingCoreReference()) {
			return null;
		}
		DefUnitSearch search = new DefUnitSearch(getCoreReferenceName(), getModuleNode(), getStartPos(), 
			findOneOnly, moduleResolver);
		performRefSearch(search);
		return search.getMatchedElements();
	}
	
	/** Return wheter this reference can match the given defunit.
	 * This is a very lightweight method that only compares the defunit's name 
	 * with the core identifier of this reference.
	 */
	@Override
	public final boolean canMatch(DefUnitDescriptor defunit) {
		return getCoreReferenceName().equals(defunit.getQualifiedId());
	}
	
}