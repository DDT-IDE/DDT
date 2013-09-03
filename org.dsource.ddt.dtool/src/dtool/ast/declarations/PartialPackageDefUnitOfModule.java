package dtool.ast.declarations;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.Collections;

import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.Module;
import dtool.ast.references.RefModule;
import dtool.ast.references.RefModule.LightweightModuleProxy;
import dtool.resolver.CommonDefUnitSearch;
import dtool.resolver.ReferenceResolver;
import dtool.util.NewUtils;

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
	public void resolveSearchInScope(CommonDefUnitSearch search) {
		if(module != null) {
			ReferenceResolver.findInNodeList(search, Collections.singleton(module), false);
		} else {
			LightweightModuleProxy targetModuleProxy = moduleRef.findTargetDefElement(search.getModuleResolver());
			if(targetModuleProxy != null) {
				Module targetModule = targetModuleProxy.resolveDefUnit();
				if(targetModule != null) {
					ReferenceResolver.findInNodeList(search, Collections.singleton(targetModule), false);
				}
			}
		}
	}
	
}