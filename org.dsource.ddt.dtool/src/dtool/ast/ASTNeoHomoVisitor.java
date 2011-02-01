package dtool.ast;

import melnorme.utilbox.tree.IVisitable;

/** 
 * An abstract visitor that visits nodes in a homogeneous way, 
 * i.e., without any type-specific methods. Uses the accept0 mechanism and
 * not getChildren().
 */
public abstract class ASTNeoHomoVisitor extends ASTNeoUpTreeVisitor {

	public <T extends IASTNeoVisitor> void traverse(IVisitable<? super IASTNeoVisitor> elem) {
		elem.accept(this);
	}
	
	@Override
	public void preVisit(ASTNeoNode elem) {
	}

	@Override
	public void postVisit(ASTNeoNode elem) {
	}
	
	abstract boolean enterNode(ASTNeoNode elem);
	abstract void leaveNode(ASTNeoNode elem);


	@Override
	public final boolean visit(ASTNeoNode elem) {
		return enterNode(elem);
	}

	@Override
	public final void endVisit(ASTNeoNode elem) {
		leaveNode(elem);
	}

}