package dtool.ast;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;


/**
 * Finds the innermost element whose source range contains the offset.
 * An element is picked between element.startPos (inclusive) and element.endPos (inclusive according to inclusiveEnd).
 */
public class ASTNodeFinder extends ASTVisitor {
	
	public static ASTNode findElement(ASTNode root, int offset) {
		return findElement(root, offset, true);
	}
	
	public static ASTNode findElement(final ASTNode root, int offset, boolean inclusiveEnd) {
		if(root == null)
			return null;
		return new ASTNodeFinder(root, offset, inclusiveEnd).match;
	}
	
	public static ASTNode findElementPreferLeft(ASTNode root, int offset) {
		ASTNodeFinder astNodeFinder = new ASTNodeFinder(root, offset, true);
		return astNodeFinder.matchOnLeft != null ? astNodeFinder.matchOnLeft : astNodeFinder.match;
	}
	
	public final ASTNode root;
	public final int offset; 
	public final boolean inclusiveEnd;
	public ASTNode match;
	public ASTNode matchOnLeft;
	
	public ASTNodeFinder(ASTNode root, int offset, boolean inclusiveEnd) {
		this(root, offset, inclusiveEnd, null);
		findNodeInAST();
	}
	
	/** Constructor that doesn't run visitor search */
	protected ASTNodeFinder(ASTNode root, int offset, boolean inclusiveEnd, @SuppressWarnings("unused") Object dummy) {
		assertNotNull(root);
		assertTrue(root.hasSourceRangeInfo());
		this.root = root;
		this.offset = offset;
		this.inclusiveEnd = inclusiveEnd;
		assertTrue(offset >= root.getStartPos() && offset <= root.getEndPos());
		
		this.match = null;
		this.matchOnLeft = null;
	}
	
	protected ASTNodeFinder findNodeInAST() {
		assertTrue(match == null && matchOnLeft == null);
		root.accept(this);
		return this;
	}
	
	@Override
	public boolean preVisit(ASTNode node) {
		if(!node.hasSourceRangeInfo()) {
			return false; // Shouldn't happen, but no need to assert
		}
		
		return findOnNode(node);
	}
	
	public boolean findOnNode(ASTNode node) {
		if(matchesNodeStart(node) && matchesNodeEnd(node)) {
			// This node is the match, or is parent of the match.
			ASTNode oldMatch = match;
			match = node;
			if(oldMatch != null && oldMatch.getEndPos() == match.getStartPos()) {
				assertTrue(offset == oldMatch.getEndPos());
				matchOnLeft = oldMatch;
			}
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