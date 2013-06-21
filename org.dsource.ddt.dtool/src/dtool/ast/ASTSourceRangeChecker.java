package dtool.ast;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;


/**
 * Checks the AST source range contracts.
 */
public class ASTSourceRangeChecker extends ASTHomogenousVisitor {
	
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
	public boolean preVisit(ASTNode elem) {
		depth++;
		if(elem.hasNoSourceRangeInfo()) {
			return handleSourceRangeNoInfo(elem);
		} else if(elem.getOffset() < offsetCursor) {
			handleSourceRangeStartPosBreach(elem);
			return false;
		}
		offsetCursor = elem.getOffset();
		return visitChildrenAfterPreVisitOk(); // Go to children
	}
	
	public boolean visitChildrenAfterPreVisitOk() {
		return true;
	}
	
	@Override
	public void postVisit(ASTNode elem) {
		depth--;
		if(elem.hasNoSourceRangeInfo()) {
			return;
		} else if(elem.getEndPos() < offsetCursor) {
			handleSourceRangeEndPosBreach(elem);
			return;
		} else {
			offsetCursor = elem.getEndPos();
			return;
		}
	}
	
	@SuppressWarnings("unused") 
	protected void handleSourceRangeEndPosBreach(ASTNode elem) {
		assertFail();
	}
	
	@SuppressWarnings("unused") 
	protected boolean handleSourceRangeNoInfo(ASTNode elem) {
		throw assertFail();
	}
	
	@SuppressWarnings("unused") 
	protected void handleSourceRangeStartPosBreach(ASTNode elem) {
		throw assertFail();
	}
	
}