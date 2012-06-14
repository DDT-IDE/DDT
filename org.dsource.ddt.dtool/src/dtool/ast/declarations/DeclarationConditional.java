package dtool.ast.declarations;

import java.util.Iterator;

import melnorme.utilbox.misc.ChainedIterator;
import melnorme.utilbox.misc.IteratorUtil;
import dtool.ast.ASTNeoNode;
import dtool.ast.NodeList;
import dtool.ast.SourceRange;
import dtool.ast.statements.IStatement;
import dtool.refmodel.INonScopedBlock;

public abstract class DeclarationConditional extends ASTNeoNode implements IStatement, INonScopedBlock {
	
	public final NodeList thenDecls;
	public final NodeList elseDecls;
	
	public DeclarationConditional(NodeList thenDecls, NodeList elseDecls, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.thenDecls = NodeList.parentizeNodeList(thenDecls, this);
		//assertNotNull(thenDecls);
		this.elseDecls = NodeList.parentizeNodeList(elseDecls, this);
	}
	
	@Override
	public Iterator<ASTNeoNode> getMembersIterator() {
		if(thenDecls == null && elseDecls == null)
			return IteratorUtil.getEMPTY_ITERATOR();
		if(thenDecls == null)
			return elseDecls.nodes.iterator();
		if(elseDecls == null)
			return thenDecls.nodes.iterator();
		
		return new ChainedIterator<ASTNeoNode>(thenDecls.nodes.iterator(), elseDecls.nodes.iterator()); 
	}
	
}