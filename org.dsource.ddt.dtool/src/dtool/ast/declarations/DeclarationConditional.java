package dtool.ast.declarations;

import java.util.Arrays;
import java.util.Iterator;

import melnorme.utilbox.misc.ArrayUtil;
import dtool.ast.ASTNeoNode;
import dtool.ast.statements.IStatement;
import dtool.refmodel.INonScopedBlock;

public abstract class DeclarationConditional extends ASTNeoNode implements IStatement, INonScopedBlock {
	
	public NodeList thendecls;
	public NodeList elsedecls;
	
	protected ASTNeoNode[] getMembers() {
		if(thendecls == null && elsedecls == null)
			return ASTNeoNode.NO_ELEMENTS;
		if(thendecls == null)
			return elsedecls.nodes;
		if(elsedecls == null)
			return thendecls.nodes;
		
		return ArrayUtil.concat(thendecls.nodes, elsedecls.nodes);
	}
	
	@Override
	public Iterator<ASTNeoNode> getMembersIterator() {
		return Arrays.asList(getMembers()).iterator();
	}
	
}
