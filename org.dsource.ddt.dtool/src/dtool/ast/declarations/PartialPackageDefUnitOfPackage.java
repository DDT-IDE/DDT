package dtool.ast.declarations;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.Collections;

import dtool.resolver.CommonDefUnitSearch;
import dtool.resolver.ReferenceResolver;


public class PartialPackageDefUnitOfPackage extends PartialPackageDefUnit  {
	
	protected final PartialPackageDefUnit subPackage; // Non-structural member
	
	protected PartialPackageDefUnitOfPackage(String defName, PartialPackageDefUnit subPackage) {
		super(defName);
		this.subPackage = parentize(assertNotNull(subPackage));
	}
	
	@Override
	public String toStringMemberName() {
		return subPackage.getName() + "." + subPackage.toStringMemberName();
	}
	
	@Override
	public void resolveSearchInScope(CommonDefUnitSearch search) {
		ReferenceResolver.findInNodeList(search, Collections.singleton(subPackage), false);
	}
	
}