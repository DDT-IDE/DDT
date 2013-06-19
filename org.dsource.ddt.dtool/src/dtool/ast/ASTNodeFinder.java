package dtool.ast;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.utilbox.core.Assert;


/**
 * Finds the innermost element whose source range contains the offset.
 * An element is picked between element.startPos (inclusive) and  
 * element.endPos (inclusive).
 */
public class ASTNodeFinder {
	
	private int offset; 
	private boolean inclusiveEnd;
	private ASTNode match;
	
	public ASTNodeFinder() {
	}
	
	public static ASTNode findElement(ASTNode root, int offset) {
		return findElement(root, offset, true);
	}
	
	public static ASTNode findElement(final ASTNode root, int offset, boolean inclusiveEnd) {
		ASTNodeFinder astNodeFinder = new ASTNodeFinder();
		return astNodeFinder.doFindElementInAST(root, offset, inclusiveEnd);
	}
	
	/** Finds the node at the given offset, starting from given root node.
	 *  Given inclusiveEnd controls whether to match nodes whose end position is the same as the offset.*/
	protected ASTNode doFindElementInAST(ASTNode root, int offsetCursor, boolean inclusiveEnd) {
		if(root == null)
			return null;
		assertTrue(root.hasSourceRangeInfo());
		
		this.offset = offsetCursor;
		this.inclusiveEnd = inclusiveEnd;
		this.match = null;
		
		if(!matchesRangeStart(root) || !matchesRangeEnd(root)) 
			return null;
		
		root.accept(new ASTHomogenousVisitor() {
			@Override
			public boolean preVisit(ASTNode node) {
				return visitNode(node);
			}
		});
		
		Assert.isNotNull(this.match);
		return match;
	}
	
	public boolean visitNode(ASTNode node) {
		if(node.hasNoSourceRangeInfo()) {
			// TODO: change to false
			return true; // Shouldn't happen, but no need to assert
		} else if(matchesRangeStart(node) && matchesRangeEnd(node)) {
			// This node is the match, or is parent of the match.
			match = node;
			return true; // Descend and search children.
		} else {
			// Match not here, don't bother descending.
			return false; 
		}
	}
	
	private boolean matchesRangeStart(ASTNode node) {
		return offset >= node.getStartPos();
	}
	
	private boolean matchesRangeEnd(ASTNode node) {
		return inclusiveEnd ? offset <= node.getEndPos() : offset < node.getEndPos();
	}
	
}
