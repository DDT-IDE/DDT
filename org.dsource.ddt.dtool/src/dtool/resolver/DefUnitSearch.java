package dtool.resolver;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import melnorme.utilbox.core.fntypes.Function;
import melnorme.utilbox.misc.StringUtil;
import dtool.ast.declarations.PackageNamespace;
import dtool.ast.definitions.INamedElement;
import dtool.ast.definitions.Module;
import dtool.ast.util.NamedElementUtil;
import dtool.project.IModuleResolver;

/**
 * Normal DefUnit search, 
 * searches for DefUnit's whose defname matches the search name. 
 */
public class DefUnitSearch extends CommonDefUnitSearch {
	
	protected final String searchName;
	
	private ArrayList<INamedElement> namedElements;
	protected boolean matchesArePartialDefUnits = false;
	
	public DefUnitSearch(String searchName, Module refOriginModule, boolean findOneOnly, 
			IModuleResolver moduleResolver) {
		this(searchName, refOriginModule, -1, findOneOnly, moduleResolver);
	}
	
	public DefUnitSearch(String searchName, Module refOriginModule, int refOffset, boolean findOneOnly,
		IModuleResolver moduleResolver) {
		super(refOriginModule, refOffset, findOneOnly, moduleResolver);
		this.searchName = assertNotNull(searchName);
		assertTrue(searchName.isEmpty() == false);
	}
	
	public Collection<INamedElement> getMatchedElements() {
		return namedElements == null ? Collections.EMPTY_LIST : namedElements;
	}
	
	@Override
	public void addMatch(INamedElement namedElem) {
		if(namedElements == null)
			namedElements = new ArrayList<>(4);
		namedElements.add(namedElem);
		if(namedElem instanceof PackageNamespace)
			matchesArePartialDefUnits = true;
	}
	
	/** Returns if this search is complete or not. A search is complete when
	 * {@link #findOnlyOne} is set, and it has found all possible valid DefUnits. 
	 * If one match is a partial DefUnit, then the search must continue searching
	 * all scopes, because there could allways be another partial. */
	@Override
	public boolean isFinished() {
		return namedElements != null && !matchesArePartialDefUnits;
	}

	@Override
	public boolean matchesName(String defName) {
		return searchName.equals(defName);
	}
	
	@Override
	public String toString() {
		return super.toString() + StringUtil.iterToString(namedElements, "\n", new Function<INamedElement, String>() {
			@Override
			public String evaluate(INamedElement obj) {
				return NamedElementUtil.getElementTypedQualification(obj); 
			}
		});
	}
	
}