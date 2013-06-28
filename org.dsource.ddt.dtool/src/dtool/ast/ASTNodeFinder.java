package dtool.ast;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;


/**
 * Finds the innermost element whose source range contains the offset.
 * An element is picked between element.startPos (inclusive) and element.endPos (inclusive according to inclusiveEnd).
 */
public class ASTNodeFinder extends ASTHomogenousVisitor {
	
	public static ASTNode findElement(ASTNode root, int offset) {
		return findElement(root, offset, true);
	}
	
	public static ASTNode findElement(final ASTNode root, int offset, boolean inclusiveEnd) {
		if(root == null)
			return null;
		ASTNodeFinder astNodeFinder = new ASTNodeFinder(root, offset, inclusiveEnd);
		astNodeFinder.findNodeInAST();
		return astNodeFinder.match;
	}
	
	public final ASTNode root;
	public final int offset; 
	public final boolean inclusiveEnd;
	public ASTNode match;
	
	public ASTNodeFinder(ASTNode root, int offset, boolean inclusiveEnd) {
		assertNotNull(root);
		assertTrue(root.hasSourceRangeInfo());
		this.root = root;
		this.offset = offset;
		this.inclusiveEnd = inclusiveEnd;
		
		findNodeInAST();
	}
	
	/** Finds the node at the given offset, starting from given root node.
	 *  Given inclusiveEnd controls whether to match nodes whose end position is the same as the offset.
	 */
	public ASTNodeFinder findNodeInAST() {
		this.match = null;
		
		if(!matchesNodeStart(root) || !matchesNodeEnd(root)) 
			return this;
		
		root.accept(this);
		
		assertNotNull(this.match);
		return this;
	}
	
	@Override
	public boolean preVisit(ASTNode node) {
		return genericVisit(node);
	}
	
	public boolean genericVisit(ASTNode node) {
		if(node.hasNoSourceRangeInfo()) {
			return false; // Shouldn't happen, but no need to assert
		}
		
		return findOnNode(node);
	}
	
	public boolean findOnNode(ASTNode node) {
		if(matchesNodeStart(node) && matchesNodeEnd(node)) {
			// This node is the match, or is parent of the match.
			match = node;
			return true; // Descend and search children.
		} else {
			// Match not here: don't bother descending, go forward
			return false; 
		}
	}
	
	protected boolean matchesNodeStart(ASTNode node) {
		return offset >= node.getStartPos();
	}
	
	protected boolean matchesNodeEnd(ASTNode node) {
		return inclusiveEnd ? offset <= node.getEndPos() : offset < node.getEndPos();
	}
	
}