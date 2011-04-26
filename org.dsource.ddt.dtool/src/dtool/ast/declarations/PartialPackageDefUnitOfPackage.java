package dtool.ast.declarations;

import java.util.Collections;
import java.util.Iterator;

import dtool.ast.ASTNeoNode;
import dtool.ast.TokenInfo;


public class PartialPackageDefUnitOfPackage extends PartialPackageDefUnit  {
	
	PartialPackageDefUnit child;
	
	protected PartialPackageDefUnitOfPackage(TokenInfo defname) {
		super(defname);
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
