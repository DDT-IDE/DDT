package dtool.resolver;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.lang.tooling.bundles.ISemanticContext;
import dtool.ast.definitions.Module;

/**
 * Normal DefUnit search, 
 * searches for DefUnit's whose defname matches the search name. 
 */
public class DefUnitSearch extends CommonDefUnitSearch {
	
	protected final String searchName;
	
	public DefUnitSearch(String searchName, Module refOriginModule, boolean findOneOnly, 
			ISemanticContext moduleResolver) {
		this(searchName, refOriginModule, -1, findOneOnly, moduleResolver);
	}
	
	public DefUnitSearch(String searchName, Module refOriginModule, int refOffset, boolean findOneOnly,
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