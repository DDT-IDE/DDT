package dtool.ast.declarations;

import java.util.Arrays;
import java.util.Iterator;

import melnorme.utilbox.misc.ArrayUtil;
import descent.internal.compiler.parser.PROT;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.SourceRange;
import dtool.ast.statements.IStatement;
import dtool.refmodel.INonScopedBlock;

public abstract class DeclarationConditional extends ASTNeoNode implements IDeclaration, IStatement, INonScopedBlock {
	
	public NodeList thendecls;
	public NodeList elsedecls;
	
	public DeclarationConditional() {
		this.thendecls = null;
		this.elsedecls = null;
	}
	
	public DeclarationConditional(NodeList thenDecls, NodeList elseDecls, SourceRange sourceRange) {
		initSourceRange(sourceRange);
		this.thendecls = thenDecls; parentize(thenDecls);
		this.elsedecls = elseDecls; parentize(elsedecls);
	}
	
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
