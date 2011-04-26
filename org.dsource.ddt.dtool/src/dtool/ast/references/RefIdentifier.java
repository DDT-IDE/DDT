package dtool.ast.references;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.Collection;

import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.definitions.DefUnit;
import dtool.refmodel.CommonDefUnitSearch;
import dtool.refmodel.DefUnitSearch;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.PrefixDefUnitSearch;
import dtool.refmodel.ReferenceResolver;

public class RefIdentifier extends NamedReference {
	
	public final String name;
	
	public RefIdentifier(String name) {
		assertNotNull(name);
		assertTrue(name.length() > 0);
		this.name = name;
	}
	
	public RefIdentifier(String name, SourceRange sourceRange) {
		this(name);
		setSourceRange(sourceRange);
	}
	
	@Override
	public String getReferenceName() {
		return name;
	}
	
	@Override
	public void accept0(IASTNeoVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}
	
	
	@Override
	public Collection<DefUnit> findTargetDefUnits(boolean findOneOnly) {
		DefUnitSearch search = new DefUnitSearch(name, this, this.getOffset(), findOneOnly);
		doSearchForPossiblyQualifiedSingleRef(search, this);
		return search.getMatchDefUnits();
	}
	
	@Override
	public void doSearch(PrefixDefUnitSearch search) {
		doSearchForPossiblyQualifiedSingleRef(search, this);
	}
	
	/** Does a search determining the correct lookup scope when
	 * the CommonRefSingle is part of a qualified referencet. */
	public static void doSearchForPossiblyQualifiedSingleRef(CommonDefUnitSearch search, RefIdentifier refSingle) {
		// First determine the lookup scope.
		if(refSingle.getParent() instanceof CommonRefQualified) {
			CommonRefQualified parent = (CommonRefQualified) refSingle.getParent();
			// check if this single ref is the sub ref of a qualified ref
			if(parent.getQualifiedName() == refSingle) {
				// then we must do qualified search (use root as the lookup scopes)
				CommonRefQualified.doQualifiedSearch(search, parent);
				return;
			} else {
				assertTrue(parent.getQualifier() == refSingle);
				// continue using outer scope as the lookup
			}
		}
		
		IScopeNode lookupScope = ReferenceResolver.getStartingScope(refSingle);
		ReferenceResolver.findDefUnitInExtendedScope(lookupScope, search);
	}
	
	@Override
	public String toStringAsElement() {
		return name;
	}
	
}

