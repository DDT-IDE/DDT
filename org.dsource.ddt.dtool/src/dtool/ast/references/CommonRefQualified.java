package dtool.ast.references;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.Collection;

import dtool.ast.definitions.INamedElement;
import dtool.ast.expressions.Resolvable.ITemplateRefNode;
import dtool.resolver.CommonDefUnitSearch;
import dtool.resolver.api.IModuleResolver;


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
	
	public abstract int getDotOffset();
	
	public abstract Collection<INamedElement> findRootDefUnits(IModuleResolver moduleResolver);
	
	@Override
	public void performRefSearch(CommonDefUnitSearch search) {
		performQualifiedRefSearch(search);
	}
	
	public void performQualifiedRefSearch(CommonDefUnitSearch search) {
		Collection<INamedElement> defunits = findRootDefUnits(search.getModuleResolver());
		Reference.resolveSearchInMultipleContainers(defunits, search, true);
	}
	
}