package dtool.ast.references;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.Collection;

import dtool.ast.definitions.DefUnit;
import dtool.ast.expressions.Resolvable.ITemplateRefNode;
import dtool.resolver.CommonDefUnitSearch;
import dtool.resolver.IScope;
import dtool.resolver.PrefixDefUnitSearch;
import dtool.resolver.ReferenceResolver;
import dtool.resolver.api.IModuleResolver;
import dtool.resolver.api.PrefixDefUnitSearchBase.ECompletionResultStatus;


/**
 * Common class for qualified references 
 * There are two: normal qualified references and Module qualified references.
 */
public abstract class CommonRefQualified extends NamedReference implements ITemplateRefNode {
	
	public final RefIdentifier qualifiedId;
	
	public CommonRefQualified(RefIdentifier qualifiedId) {
		this.qualifiedId = parentize(assertNotNull(qualifiedId));
	}
	
	/** Return the qualified name (the name reference on the right side). */
	public RefIdentifier getQualifiedName() {
		return qualifiedId;
	}
	
	@Override
	public String getCoreReferenceName() {
		return qualifiedId.getCoreReferenceName();
	}
	
	public abstract Collection<DefUnit> findRootDefUnits(IModuleResolver moduleResolver);
	
	@Override
	public void doSearch(CommonDefUnitSearch search) {
		Collection<DefUnit> defunits = findRootDefUnits(search.getModResolver());
		findDefUnitInMultipleDefUnitScopes(defunits, search);
	}
	
	public static void findDefUnitInMultipleDefUnitScopes(Collection<DefUnit> defunits, CommonDefUnitSearch search) {
		if(defunits == null)
			return;
		
		for (DefUnit unit : defunits) {
			IScope scope = unit.getMembersScope(search.getModResolver());
			if(scope != null) {
				ReferenceResolver.findDefUnitInScope(scope, search);
			}
			if(search.isFinished())
				return;
		}
	}
	
	@Override
	public void performPrefixSearch(PrefixDefUnitSearch prefixSearch, String fullSource) {
		if(prefixSearch.getOffset() <= getDotOffset()) {
			prefixSearch.assignResult(ECompletionResultStatus.INVALID_REFQUAL_LOCATION, 
					"Invalid Location: before qualifier dot but not next to id.");
			return;
		}
		
		doSearch(prefixSearch);
	}
	
	public int getDotOffset() {
		return getStartPos();
	}
	
}