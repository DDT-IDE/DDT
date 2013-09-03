package dtool.ast.declarations;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.Collections;

import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.Module;
import dtool.ast.references.RefModule;
import dtool.resolver.CommonDefUnitSearch;
import dtool.resolver.ReferenceResolver;
import dtool.util.NewUtils;

/**
 * A package namespace containing a module.
 */
public class PartialPackageDefUnitOfModule extends PartialPackageDefUnit {
	
	protected final RefModule moduleRef;
	protected final DefUnit module;
	
	public PartialPackageDefUnitOfModule(String defName, DefUnit module, RefModule moduleRef) {
		super(defName);
		this.module = module;
		this.moduleRef = moduleRef;
		assertTrue(NewUtils.exactlyOneIsNull(module, moduleRef));
	}
	
	@Override
	public String toStringMemberName() {
		return "[" + (module != null ? 
			module.getFullyQualifiedName() :
			moduleRef.getModuleFullyQualifiedName())
			+ "]";
	}
	
	@Override
	public void resolveSearchInScope(CommonDefUnitSearch search) {
		if(module != null) {
			ReferenceResolver.findInNodeList(search, Collections.singleton(module), false);
		} else {
			ModuleProxy targetModuleProxy = moduleRef.findTargetDefElement(search.getModuleResolver());
			if(targetModuleProxy != null) {
				ReferenceResolver.findInNodeList(search, Collections.singleton(targetModuleProxy), false);
			}
		}
	}
	
}