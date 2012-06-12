package dtool.ast.declarations;

import java.util.Collections;
import java.util.Iterator;

import dtool.ast.ASTNeoNode;
import dtool.ast.TokenInfo;


public class PartialPackageDefUnitOfPackage extends PartialPackageDefUnit  {
	
	protected final PartialPackageDefUnit child;
	
	protected PartialPackageDefUnitOfPackage(TokenInfo defname, PartialPackageDefUnit child) {
		super(defname);
		this.child = child; // BUG here?
	}
	
	@Override
	public Iterator<? extends ASTNeoNode> getMembersIterator() {
		return Collections.singleton(child).iterator();
	}
	
	@Override
	public String toStringAsElement() {
		return getName() /*+ "." + child.toStringAsElement()*/;
	}
	
}