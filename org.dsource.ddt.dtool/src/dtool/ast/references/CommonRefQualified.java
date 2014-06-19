package dtool.ast.references;


import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.Collection;

import dtool.ast.definitions.INamedElement;
import dtool.ast.expressions.Resolvable.ITemplateRefNode;
import dtool.engine.modules.IModuleResolver;
import dtool.resolver.CommonDefUnitSearch;
import dtool.resolver.LanguageIntrinsics;


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
		CommonRefQualified.resolveSearchInMultipleContainers(defunits, search, true);
	}
	
	public static void resolveSearchInMultipleContainers(Collection<INamedElement> containers, 
		CommonDefUnitSearch search, boolean isDotQualified) {
		if(containers == null)
			return;
		
		boolean hasTypeContainer = false;
		for (INamedElement container : containers) {
			if(search.isFinished())
				return;
			container.resolveSearchInMembersScope(search);
			
			if(container.getArcheType().isType()) {
				hasTypeContainer = true;
			}
		}
		
		if(isDotQualified && hasTypeContainer) { 
			LanguageIntrinsics.d_2_063_intrinsics.typePropertiesScope.resolveSearchInScope(search);
		}
	}
	
}