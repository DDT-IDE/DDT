package dtool.ast;


/**
 * Sets parent entries in the tree nodes, using homogenous Visitor.
 * Assumes a neo AST. 
 */
public class ASTNodeParentizer extends ASTNeoHomogenousVisitor {
	
	private ASTNeoNode parent = null;
	
	@Override
	public boolean preVisit(ASTNeoNode elem) {
		elem.setParent(parent); // Set parent to current parent
		parent = elem; // Set as new parent
		return true;
	}
	
	@Override
	public void postVisit(ASTNeoNode elem) {
		parent = elem.getParent(); // Restore previous parent
	}
	
}

