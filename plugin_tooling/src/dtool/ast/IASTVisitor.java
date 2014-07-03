package dtool.ast;


/**
 * Visitor class for {@link ASTNode}
 */
public interface IASTVisitor {
	
	/** Visit a node and return a boolean to indicate if children should be visited or not. */
	public boolean preVisit(ASTNode node);
	
	/** Visit a node after children have (potentially) been visited. */
	public void postVisit(ASTNode node);
	
}