package dtool.ast;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;


/**
 * Checks the AST source range contracts.
 */
public class ASTSourceRangeChecker extends ASTVisitor {
	
	protected int offsetCursor;
	protected int depth = 0;
	
	public static void checkConsistency(ASTNode elem){
		new ASTSourceRangeChecker(elem);
	}
	
	public ASTSourceRangeChecker(ASTNode elem) {
		this(elem.getStartPos());
		elem.accept(this);
	}
	
	public ASTSourceRangeChecker(int offsetCursor) {
		this.offsetCursor = offsetCursor;
	}
	
	@Override
	public boolean preVisit(ASTNode node) {
		depth++;
		
		assertTrue(node.hasSourceRangeInfo());
		if(node.getOffset() < offsetCursor) {
			handleSourceRangeStartPosBreach(node);
			return false;
		}
		offsetCursor = node.getOffset();
		return visitChildrenAfterPreVisitOk(); // Go to children
	}
	
	public boolean visitChildrenAfterPreVisitOk() {
		return true;
	}
	
	@Override
	public void postVisit(ASTNode node) {
		depth--;
		
		assertTrue(node.hasSourceRangeInfo());
		if(node.getEndPos() < offsetCursor) {
			handleSourceRangeEndPosBreach(node);
			return;
		} else {
			offsetCursor = node.getEndPos();
			return;
		}
	}
	
	@SuppressWarnings("unused") 
	protected void handleSourceRangeEndPosBreach(ASTNode elem) {
		assertFail();
	}
	
	@SuppressWarnings("unused") 
	protected void handleSourceRangeStartPosBreach(ASTNode elem) {
		throw assertFail();
	}
	
}