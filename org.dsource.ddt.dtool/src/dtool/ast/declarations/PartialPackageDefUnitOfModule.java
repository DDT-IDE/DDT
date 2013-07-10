package dtool.ast.declarations;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.Collections;
import java.util.Iterator;

import melnorme.utilbox.misc.IteratorUtil;
import dtool.ast.ASTNode;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.Module;
import dtool.ast.references.RefModule;
import dtool.resolver.api.IModuleResolver;
import dtool.util.NewUtils;

public class PartialPackageDefUnitOfModule extends PartialPackageDefUnit {
	
	protected final RefModule moduleRef;
	protected final DefUnit module;
	
	public PartialPackageDefUnitOfModule(String defName, Module module, RefModule moduleRef) {
		super(defName);
		this.module = module;
		this.moduleRef = moduleRef;
		assertTrue(NewUtils.exactlyOneIsNull(module, moduleRef));
	}
	
	@Override
	public Iterator<? extends ASTNode> getMembersIterator(IModuleResolver moduleResolver) {
		if(module != null)
			return Collections.singleton(module).iterator();
		else {
			// Could we cache this result?
			Module targetModule = (Module) moduleRef.findTargetDefUnit(moduleResolver);
			if(targetModule != null)
				return Collections.singleton(targetModule).iterator();
			return IteratorUtil.getEMPTY_ITERATOR();
		}
	}
	
	@Override
	public String toStringAsElement() {
		if(module != null) {
			return getName() /*+ "." + module.toStringAsElement()*/;
		} else {
			return getName() /*+ "." + moduleRef.module*/;
		}
	}
	
}