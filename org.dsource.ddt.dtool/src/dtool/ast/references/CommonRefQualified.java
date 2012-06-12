package dtool.ast.references;


import java.util.Collection;

import dtool.ast.definitions.DefUnit;
import dtool.refmodel.CommonDefUnitSearch;
import dtool.refmodel.DefUnitSearch;
import dtool.refmodel.IDefUnitReferenceNode;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.PrefixDefUnitSearch;
import dtool.refmodel.ReferenceResolver;


/**
 * Common class for qualified references 
 * There are two: normal qualified references and Module qualified references.
 */
public abstract class CommonRefQualified extends NamedReference implements IDefUnitReferenceNode {
	
	public final RefIdentifier qualifiedName;
	
	public CommonRefQualified(RefIdentifier qualifiedName) {
		this.qualifiedName = parentize(qualifiedName);
	}
	
	/** Return the qualified name (the name reference on the right side). */
	public RefIdentifier getQualifiedName() {
		return qualifiedName;
	}
	
	/** maybe null */
	public abstract IDefUnitReferenceNode getQualifier();

	public abstract Collection<DefUnit> findRootDefUnits();
	
	@Override
	public String getReferenceName() {
		return qualifiedName.getReferenceName();
	}
	
	/** Finds the target defunits of this qualified reference. */
	@Override
	public Collection<DefUnit> findTargetDefUnits(boolean findOneOnly) {
		DefUnitSearch search = new DefUnitSearch(qualifiedName.name, this);
		doQualifiedSearch(search, this);
		return search.getMatchDefUnits();
	}
	
	@Override
	public void doSearch(PrefixDefUnitSearch search) {
		doQualifiedSearch(search, this);
	}
	
	public static void doQualifiedSearch(CommonDefUnitSearch search, CommonRefQualified qref) {
		Collection<DefUnit> defunits = qref.findRootDefUnits();
		findDefUnitInMultipleDefUnitScopes(defunits, search);
	}
	
	public static void findDefUnitInMultipleDefUnitScopes(Collection<DefUnit> defunits, CommonDefUnitSearch search) {
		if(defunits == null)
			return;
		
		for (DefUnit unit : defunits) {
			IScopeNode scope = unit.getMembersScope();
			if(scope != null) {
				ReferenceResolver.findDefUnitInScope(scope, search);
			}
			if(search.isFinished())
				return;
		}
	}
	
}