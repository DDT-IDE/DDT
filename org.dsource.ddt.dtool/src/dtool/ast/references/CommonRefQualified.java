package dtool.ast.references;


import java.util.Collection;

import dtool.ast.IASTNeoNode;
import dtool.ast.definitions.DefUnit;
import dtool.refmodel.CommonDefUnitSearch;
import dtool.refmodel.DefUnitSearch;
import dtool.refmodel.IDefUnitReference;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.PrefixDefUnitSearch;
import dtool.refmodel.ReferenceResolver;
import dtool.refmodel.pluginadapters.IModuleResolver;


/**
 * Common class for qualified references 
 * There are two: normal qualified references and Module qualified references.
 */
public abstract class CommonRefQualified extends NamedReference {
	
	public static interface IQualifierNode extends IDefUnitReference, IASTNeoNode { }
	
	public final RefIdentifier qualifiedName;
	
	public CommonRefQualified(RefIdentifier qualifiedName) {
		this.qualifiedName = parentize(qualifiedName);
	}
	
	/** Return the qualified name (the name reference on the right side). */
	public RefIdentifier getQualifiedName() {
		return qualifiedName;
	}
	
	/** maybe null */
	public abstract IQualifierNode getQualifier();
	
	public abstract Collection<DefUnit> findRootDefUnits(IModuleResolver moduleResolver);
	
	@Override
	public String getReferenceName() {
		return qualifiedName.getReferenceName();
	}
	
	/** Finds the target defunits of this qualified reference. */
	@Override
	public Collection<DefUnit> findTargetDefUnits(IModuleResolver moduleResolver, boolean findOneOnly) {
		DefUnitSearch search = new DefUnitSearch(qualifiedName.name, this, findOneOnly, moduleResolver);
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