package dtool.ast.references;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.Collection;

import dtool.ast.definitions.DefUnit;
import dtool.ast.expressions.Resolvable.ITemplateRefNode;
import dtool.resolver.CommonDefUnitSearch;
import dtool.resolver.PrefixDefUnitSearch;
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
		Collection<DefUnit> defunits = findRootDefUnits(search.getModuleResolver());
		resolveSearchInMultipleDefUnits(defunits, search);
	}
	
	public static void resolveSearchInMultipleDefUnits(Collection<DefUnit> defUnits, CommonDefUnitSearch search) {
		if(defUnits == null)
			return;
		
		for (DefUnit defUnit : defUnits) {
			if(search.isFinished())
				return;
			defUnit.resolveSearchInMembersScope(search);
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