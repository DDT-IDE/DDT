package dtool.ast;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import dtool.Logg;


/**
 * Checks for AST validity. Namely:
 * Source range consistency. 
 */
public class ASTCommonSourceRangeChecker extends ASTNeoHomogenousVisitor {
	
	@Deprecated
	/** Checks an AST for errors, such as source range errors. */
	public static void checkConsistency(ASTNeoNode elem){
		elem.accept(new ASTCommonSourceRangeChecker(elem.getStartPos()));
	}
	
	protected int offsetCursor;
	protected int depth = 0;
	
	public ASTCommonSourceRangeChecker(int offsetCursor) {
		this.offsetCursor = offsetCursor;
	}
	
	@Override
	public boolean preVisit(ASTNeoNode elem) {
		depth++;
		if(elem.hasNoSourceRangeInfo()) {
			return handleSourceRangeNoInfo(elem);
		} else if(elem.getOffset() < offsetCursor) {
			return handleSourceRangeStartPosBreach(elem);
		} else {
			offsetCursor = elem.getOffset();
			return visitChildrenAfterPreVisitOk(); // Go to children
		}
	}
	
	public boolean visitChildrenAfterPreVisitOk() {
		return true;
	}
	
	@Override
	public void postVisit(ASTNeoNode elem) {
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
	
	/* ====================================================== */
	
	protected boolean handleSourceRangeNoInfo(ASTNeoNode elem) {
//		assertFail();
		Logg.astmodel.print("Source range no info on: ");
		Logg.astmodel.println(elem.toStringAsNode(true));
		return false;
	}
	
	protected boolean handleSourceRangeStartPosBreach(ASTNeoNode elem) {
//		assertFail();
		Logg.astmodel.print("Source range start-pos error on: ");
		Logg.astmodel.println(elem.toStringAsNode(true));
		return false;
	}
	
	protected void handleSourceRangeEndPosBreach(ASTNeoNode elem) {
//		assertFail();
		Logg.astmodel.print("Source range end-pos error on: ");
		Logg.astmodel.println(elem.toStringAsNode(true));
	}
	
	public static class ASTSourceRangeChecker extends ASTCommonSourceRangeChecker {
		
		public static void checkConsistency(ASTNeoNode elem){
			new ASTSourceRangeChecker(elem);
		}
		
		public ASTSourceRangeChecker(ASTNeoNode elem) {
			super(elem.getStartPos());
			elem.accept(this);
		}
		
		public ASTSourceRangeChecker(int offsetCursor) {
			super(offsetCursor);
		}
		
		@Override
		protected void handleSourceRangeEndPosBreach(ASTNeoNode elem) {
			assertFail();
		}
		
		@Override
		protected boolean handleSourceRangeNoInfo(ASTNeoNode elem) {
			throw assertFail();
		}
		
		@Override
		protected boolean handleSourceRangeStartPosBreach(ASTNeoNode elem) {
			throw assertFail();
		}
	}
	
}