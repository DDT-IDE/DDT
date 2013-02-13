package dtool.ast;

import melnorme.utilbox.core.Assert;
import descent.internal.compiler.parser.ast.ASTNode;
import descent.internal.compiler.parser.ast.ASTUpTreeVisitor;
import descent.internal.compiler.parser.ast.IASTNode;


/**
 * Finds the innermost element whose source range contains the offset.
 * An element is picked between element.startPos (inclusive) and  
 * element.endPos (inclusive).
 */
public abstract class ASTNodeFinder<T extends IASTNode> {
	
	private int offset; 
	private boolean inclusiveEnd;
	private T match;
	
	public ASTNodeFinder() {
	}
	
	public static ASTNode findElement(ASTNode root, int offset) {
		return findElement(root, offset, true);
	}
	
	public static ASTNode findElement(final ASTNode root, int offset, boolean inclusiveEnd) {
		ASTNodeFinder<ASTNode> astNodeFinder = new ASTNodeFinder<ASTNode>() {
			@Override
			public void doAcceptOnRoot() {
				root.accept(new ASTUpTreeVisitor() {
					@Override
					public boolean visit(ASTNode node) {
						return visitNode(node);
					}
				});
			}
		};
		return astNodeFinder.doFindElementInAST(root, offset, inclusiveEnd);
	}
	
	public static ASTNeoNode findElement(ASTNeoNode root, int offset) {
		return findElement(root, offset, true);
	}
	
	public static ASTNeoNode findElement(final ASTNeoNode root, int offset, boolean inclusiveEnd) {
		ASTNodeFinder<ASTNeoNode> astNodeFinder = new ASTNodeFinder<ASTNeoNode>() {
			@Override
			public void doAcceptOnRoot() {
				root.accept(new ASTHomogenousVisitor() {
					@Override
					public boolean preVisit(ASTNeoNode node) {
						return visitNode(node);
					}
				});
			}
		};
		return astNodeFinder.doFindElementInAST(root, offset, inclusiveEnd);
	}
	
	/** Finds the node at the given offset, starting from given root node.
	 *  Given inclusiveEnd controls whether to match nodes whose end position is the same as the offset.*/
	protected T doFindElementInAST(T root, int offsetCursor, boolean inclusiveEnd) {
		if(root == null)
			return null;
		Assert.isTrue(!root.hasNoSourceRangeInfo());
		
		this.offset = offsetCursor;
		this.inclusiveEnd = inclusiveEnd;
		this.match = null;
		
		if(!matchesRangeStart(root) || !matchesRangeEnd(root)) 
			return null;
		
		this.doAcceptOnRoot();
		
		Assert.isNotNull(this.match);
		return match;
	}
	
	protected abstract void doAcceptOnRoot();
	
	public boolean visitNode(T elem) {
		if(elem.hasNoSourceRangeInfo()) {
			//Assert.fail();
			return true; // Descend and search children.
		} else if(matchesRangeStart(elem) && matchesRangeEnd(elem)) {
			// This node is the match, or is parent of the match.
			match = elem;
			return true; // Descend and search children.
		} else {
			// Match not here, don't bother descending.
			return false; 
		}
	}
	
	private boolean matchesRangeStart(IASTNode elem) {
		return offset >= elem.getStartPos();
	}
	
	private boolean matchesRangeEnd(IASTNode elem) {
		return inclusiveEnd ? offset <= elem.getEndPos() : offset < elem.getEndPos();
	}
	
}
