package dtool.ast.definitions;

import melnorme.utilbox.tree.TreeVisitor;
import descent.internal.compiler.parser.PROT;
import dtool.ast.ASTNeoNode;
import dtool.ast.IASTVisitor;
import dtool.ast.references.Reference;

public class BaseClass extends ASTNeoNode {
	
	public final PROT prot;
	public final Reference type;
	
	public BaseClass(PROT prot, Reference type) {
		this.prot = prot;
		this.type = parentize(type);
	}
	
	@Override
	public void accept0(IASTVisitor visitor) {
		boolean children = visitor.visit(this);
		if (children) {
			TreeVisitor.acceptChildren(visitor, type);
		}
		visitor.endVisit(this);	 			
	}
	
}