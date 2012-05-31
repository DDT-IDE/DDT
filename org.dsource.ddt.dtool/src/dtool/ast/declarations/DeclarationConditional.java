package dtool.ast.declarations;

import java.util.Arrays;
import java.util.Iterator;

import melnorme.utilbox.misc.ArrayUtil;
import descent.internal.compiler.parser.PROT;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTNeoVisitor;
import dtool.ast.statements.IStatement;
import dtool.refmodel.INonScopedBlock;

public abstract class DeclarationConditional extends ASTNeoNode implements IDeclaration, IStatement, INonScopedBlock {
	
	public NodeList thendecls;
	public NodeList elsedecls;
	
	public DeclarationConditional() {
		this.thendecls = null;
		this.elsedecls = null;
	}
	
	public DeclarationConditional(NodeList thenDecls, NodeList elseDecls) {
		this.thendecls = thenDecls;
		if (this.thendecls != null) {
			for (ASTNeoNode n: this.thendecls.nodes) {
				n.setParent(this);
			}
		}
		this.elsedecls = elseDecls;
		if (this.elsedecls != null) {
			for (ASTNeoNode n: this.elsedecls.nodes) {
				n.setParent(this);
			}
		}
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

	@Override
	public void setAttributes(int effectiveModifiers) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getAttributes() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setProtection(PROT prot) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PROT getEffectiveProtection() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void accept0(IASTNeoVisitor visitor) {
		// TODO Auto-generated method stub
		
	}
	
}
