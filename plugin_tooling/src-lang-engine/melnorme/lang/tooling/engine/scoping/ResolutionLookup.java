package melnorme.lang.tooling.engine.scoping;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.lang.tooling.ast.IModuleElement;
import melnorme.lang.tooling.bundles.ISemanticContext;

/**
 * Normal DefUnit search, 
 * searches for DefUnit's whose defname matches the search name. 
 */
public class ResolutionLookup extends CommonScopeLookup {
	
	protected final String searchName;
	
	public ResolutionLookup(String searchName, IModuleElement refOriginModule, boolean findOneOnly, 
			ISemanticContext moduleResolver) {
		this(searchName, refOriginModule, -1, findOneOnly, moduleResolver);
	}
	
	public ResolutionLookup(String searchName, IModuleElement refOriginModule, int refOffset, boolean findOneOnly,
		ISemanticContext moduleResolver) {
		super(refOriginModule, refOffset, findOneOnly, moduleResolver);
		this.searchName = assertNotNull(searchName);
		assertTrue(searchName.isEmpty() == false);
	}
	
	/** Returns if this search is complete or not. A search is complete when
	 * {@link #findOnlyOne} is set, and it has found all possible valid DefUnits. 
	 * If one match is a partial DefUnit, then the search must continue searching
	 * all scopes, because there could allways be another partial. */
	@Override
	public boolean isFinished() {
		return !getMatchedElements().isEmpty() && !matchesArePartialDefUnits;
	}
	
	@Override
	public boolean matchesName(String defName) {
		return searchName.equals(defName);
	}
	
	
}