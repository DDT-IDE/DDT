package dtool.ast.references;


import java.util.Collection;

import dtool.ast.definitions.DefUnit;
import dtool.ast.expressions.Resolvable.ITemplateRefNode;
import dtool.refmodel.CommonDefUnitSearch;
import dtool.refmodel.DefUnitSearch;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.PrefixDefUnitSearch;
import dtool.refmodel.ReferenceResolver;
import dtool.refmodel.api.IModuleResolver;


/**
 * Common class for qualified references 
 * There are two: normal qualified references and Module qualified references.
 */
public abstract class CommonRefQualified extends NamedReference implements ITemplateRefNode {
	
	public final RefIdentifier qualifiedId;
	
	public CommonRefQualified(RefIdentifier qualifiedId) {
		this.qualifiedId = parentize(qualifiedId);
	}
	
	/** Return the qualified name (the name reference on the right side). */
	public RefIdentifier getQualifiedName() {
		return qualifiedId;
	}
	
	public abstract Collection<DefUnit> findRootDefUnits(IModuleResolver moduleResolver);
	
	@Override
	public String getTargetSimpleName() {
		return qualifiedId.getTargetSimpleName();
	}
	
	/** Finds the target defunits of this qualified reference. */
	@Override
	public Collection<DefUnit> findTargetDefUnits(IModuleResolver moduleResolver, boolean findOneOnly) {
		DefUnitSearch search = new DefUnitSearch(qualifiedId.getIdString(), this, findOneOnly, moduleResolver);
		doQualifiedSearch(search, this);
		return search.getMatchDefUnits();
	}
	
	@Override
	public void doSearch(PrefixDefUnitSearch search) {
		doQualifiedSearch(search, this);
	}
	
	public static void doQualifiedSearch(CommonDefUnitSearch search, CommonRefQualified qref) {
		Collection<DefUnit> defunits = qref.findRootDefUnits(search.getModResolver());
		findDefUnitInMultipleDefUnitScopes(defunits, search);
	}
	
	public static void findDefUnitInMultipleDefUnitScopes(Collection<DefUnit> defunits, CommonDefUnitSearch search) {
		if(defunits == null)
			return;
		
		for (DefUnit unit : defunits) {
			IScopeNode scope = unit.getMembersScope(search.getModResolver());
			if(scope != null) {
				ReferenceResolver.findDefUnitInScope(scope, search);
			}
			if(search.isFinished())
				return;
		}
	}
	
}