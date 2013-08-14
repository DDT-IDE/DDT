package dtool.resolver;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import dtool.ast.declarations.PartialPackageDefUnit;
import dtool.ast.definitions.DefUnit;
import dtool.ast.references.Reference;
import dtool.resolver.api.IModuleResolver;

/**
 * Normal DefUnit search, 
 * searches for DefUnit's whose defname matches the search name. 
 */
public class DefUnitSearch extends CommonDefUnitSearch {

	protected final String searchName;

	private ArrayList<DefUnit> defunits;
	protected boolean matchesArePartialDefUnits = false;
	
	public DefUnitSearch(String searchName, Reference searchref, boolean findOneOnly, IModuleResolver moduleResolver) {
		this(searchName, searchref, -1, findOneOnly, moduleResolver);
	}
	
	public DefUnitSearch(String searchName, Reference searchref, int refOffset, boolean findOneOnly,
			IModuleResolver moduleResolver) {
		super(searchref, refOffset, findOneOnly, moduleResolver);
		this.searchName = assertNotNull(searchName);
		assertTrue(searchName.isEmpty() == false);
	}
	
	public Collection<DefUnit> getMatchDefUnits() {
		return defunits == null ? Collections.EMPTY_LIST : defunits;
	}
	
	@Override
	public void addMatch(DefUnit defunit) {
		if(defunits == null)
			defunits = new ArrayList<DefUnit>(4);
		defunits.add(defunit);
		if(defunit instanceof PartialPackageDefUnit)
			matchesArePartialDefUnits = true;
	}
	
	/** Returns if this search is complete or not. A search is complete when
	 * {@link #findOnlyOne} is set, and it has found all possible valid DefUnits. 
	 * If one match is a partial DefUnit, then the search must continue searching
	 * all scopes, because there could allways be another partial. */
	@Override
	public boolean isFinished() {
		return defunits != null && !matchesArePartialDefUnits;
	}

	@Override
	public boolean matchesName(String defName) {
		return searchName.equals(defName);
	}

}