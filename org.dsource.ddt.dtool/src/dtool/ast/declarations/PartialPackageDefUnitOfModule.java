package dtool.ast.declarations;

import java.util.Collections;
import java.util.Iterator;

import melnorme.utilbox.misc.IteratorUtil;
import dtool.ast.ASTNeoNode;
import dtool.ast.TokenInfo;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.Module;
import dtool.ast.references.RefModule;

public class PartialPackageDefUnitOfModule extends PartialPackageDefUnit {
	
	protected final RefModule moduleRef;
	protected final DefUnit module;
	
	public PartialPackageDefUnitOfModule(TokenInfo name, Module module, RefModule moduleRef) {
		super(name);
		this.module = module; // BUG here
		this.moduleRef = moduleRef;
	}
	
	@Override
	public Iterator<? extends ASTNeoNode> getMembersIterator() {
		if(module != null)
			return Collections.singleton(module).iterator();
		else {
			// Could we cache this result?
			Module targetModule = (Module) moduleRef.findTargetDefUnit();
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