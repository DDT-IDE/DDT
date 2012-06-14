package dtool.ast.declarations;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.Collections;
import java.util.Iterator;

import dtool.ast.ASTNeoNode;
import dtool.refmodel.pluginadapters.IModuleResolver;


public class PartialPackageDefUnitOfPackage extends PartialPackageDefUnit  {
	
	protected final PartialPackageDefUnit subPackage; // Non-structural member
	
	protected PartialPackageDefUnitOfPackage(String defName, PartialPackageDefUnit subPackage) {
		super(defName);
		this.subPackage = subPackage;
		assertNotNull(subPackage);
	}
	
	@Override
	public Iterator<? extends ASTNeoNode> getMembersIterator(IModuleResolver moduleResolver) {
		return Collections.singleton(subPackage).iterator();
	}
	
	@Override
	public String toStringAsElement() {
		return getName() /*+ "." + child.toStringAsElement()*/;
	}
	
}