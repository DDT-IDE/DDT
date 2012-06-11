package dtool.ast.declarations;

import java.util.Iterator;

import melnorme.utilbox.misc.ChainedIterator;
import melnorme.utilbox.misc.IteratorUtil;
import dtool.ast.ASTNeoNode;
import dtool.ast.SourceRange;
import dtool.ast.statements.IStatement;
import dtool.refmodel.INonScopedBlock;

public abstract class DeclarationConditional extends ASTNeoNode implements IStatement, INonScopedBlock {
	
	public final NodeList thendecls;
	public final NodeList elsedecls;
	
	public DeclarationConditional(NodeList thenDecls, NodeList elseDecls, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.thendecls = thenDecls; parentize(thenDecls);
		this.elsedecls = elseDecls; parentize(elsedecls);
	}
	
	@Override
	public Iterator<ASTNeoNode> getMembersIterator() {
		if(thendecls == null && elsedecls == null)
			return IteratorUtil.getEMPTY_ITERATOR();
		if(thendecls == null)
			return elsedecls.nodes.iterator();
		if(elsedecls == null)
			return thendecls.nodes.iterator();
		
		return new ChainedIterator<ASTNeoNode>(thendecls.nodes.iterator(), elsedecls.nodes.iterator()); 
	}
	
}
