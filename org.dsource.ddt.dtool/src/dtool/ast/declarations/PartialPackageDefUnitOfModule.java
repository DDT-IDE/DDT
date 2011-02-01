package dtool.ast.declarations;

import java.util.Collections;
import java.util.Iterator;

import melnorme.utilbox.misc.IteratorUtil;
import dtool.ast.ASTNeoNode;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.Module;
import dtool.ast.definitions.Symbol;
import dtool.ast.references.RefModule;

public class PartialPackageDefUnitOfModule extends PartialPackageDefUnit {

	RefModule moduleRef;
	DefUnit module;
	
	public PartialPackageDefUnitOfModule(Symbol name) {
		super(name);
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
		if(module != null)
			return getName() /*+ "." + module.toStringAsElement()*/;
		else {
			return getName() /*+ "." + moduleRef.module*/;
		}
	}


}
