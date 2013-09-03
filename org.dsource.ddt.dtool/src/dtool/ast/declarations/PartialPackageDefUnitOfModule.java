package dtool.ast.declarations;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.INamedElement;
import dtool.resolver.CommonDefUnitSearch;
import dtool.resolver.ReferenceResolver;

/**
 * A package namespace containing a module.
 */
public class PartialPackageDefUnitOfModule extends PartialPackageDefUnit {
	
	protected final INamedElement moduleElement;
	
	public PartialPackageDefUnitOfModule(String defName, INamedElement module) {
		super(defName);
		this.moduleElement = assertNotNull(module);
	}
	
	@Override
	public String toStringMemberName() {
		return "[" + moduleElement.getFullyQualifiedName()	+ "]";
	}
	
	@Override
	public void resolveSearchInScope(CommonDefUnitSearch search) {
		DefUnit resolvedDefUnit = moduleElement.resolveDefUnit();
		if(resolvedDefUnit != null) {
			// Note that we dont use resolvedDefUnit for evaluateNodeForSearch,
			// this means modules with mismatched names will match again the import name (file name),
			// instead of the module declaration name
			ReferenceResolver.evaluateNodeForSearch(search, false, false, moduleElement);
		}
	}
	
}