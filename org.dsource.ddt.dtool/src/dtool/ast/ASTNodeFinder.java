package dtool.ast;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import melnorme.utilbox.core.Assert;
import melnorme.utilbox.core.CoreUtil;
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
	
	private IASTNode match = null;
	
	public ASTNodeFinder() {
	}
	
	public static IASTNode findElement(IASTNode root, int offset) {
		return findElement(root, offset, true);
	}
	
	public static ASTNeoNode findElement(ASTNeoNode root, int offset) {
		return findElement(root, offset, true);
	}
	
	/** Finds the node at the given offset, starting from root.
	 *  inclusiveEnd controls whether to match nodes whose end position 
	 *  is the same as the offset.*/
	public static <T extends IASTNode> T findElement(T root, int offset, boolean inclusiveEnd) {
		IASTNode match;
		if(root instanceof ASTNeoNode) {
			ASTNeoNode rootX = (ASTNeoNode) root;
			ASTNodeFinder<ASTNeoNode> astNodeFinder = new ASTNodeFinder<ASTNeoNode>() {
				@Override
				protected void doVisit(ASTNeoNode root) {
					final ASTNodeFinder<ASTNeoNode> visitor = this;
					root.accept(new ASTNeoHomogenousVisitor() {
						@Override
						public boolean preVisit(ASTNeoNode node) {
							return visitor.visit(node);
						}
					});
				}
			};
			match = astNodeFinder.acceptDependingOnKind(rootX, offset, inclusiveEnd);
			
		} else if(root instanceof ASTNode) {
			ASTNode rootX = (ASTNode) root;
			ASTNodeFinder<ASTNode> astNodeFinder = new ASTNodeFinder<ASTNode>() {
				@Override
				protected void doVisit(ASTNode root) {
					final ASTNodeFinder<ASTNode> visitor = this;
					root.accept(new ASTUpTreeVisitor() {
						@Override
						public boolean visit(ASTNode node) {
							return visitor.visit(node);
						}
					});
				}
			};
			match = astNodeFinder.acceptDependingOnKind(rootX, offset, inclusiveEnd);
			
		} else {
			throw assertFail();
		}
		
		return CoreUtil.<IASTNode, T>downCast(match);
	}
	
	protected IASTNode acceptDependingOnKind(T root, int offsetCursor, boolean inclusiveEnd) {
		this.offset = offsetCursor;
		this.inclusiveEnd = inclusiveEnd;
		
		if(root == null)
			return null;
		Assert.isTrue(!root.hasNoSourceRangeInfo());
		
		if(!matchesRangeStart(root) || !matchesRangeEnd(root)) 
			return null;
		
		doVisit(root);
		
		Assert.isNotNull(this.match);
		return match;
	}

	protected abstract void doVisit(T root);
	
	public boolean visit(IASTNode elem) {
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
