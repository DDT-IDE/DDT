package dtool.ast;


/** 
 * An abstract visitor that visits nodes in a homogeneous way, 
 * i.e., without any type-specific methods. Uses the accept0 mechanism and
 * not getChildren().
 */
@Deprecated
public abstract class ASTNeoHomoVisitor extends ASTNeoUpTreeVisitor {

	@Override
	public void preVisit(ASTNeoNode elem) {
	}
	
	@Override
	public void postVisit(ASTNeoNode elem) {
	}
	
	protected boolean enterNode(ASTNeoNode elem) { return true; }
	protected void leaveNode(ASTNeoNode elem) { }


	@Override
	public final boolean visit(ASTNeoNode elem) {
		return enterNode(elem);
	}

	@Override
	public final void endVisit(ASTNeoNode elem) {
		leaveNode(elem);
	}

}