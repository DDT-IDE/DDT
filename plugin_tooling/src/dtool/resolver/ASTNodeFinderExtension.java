package dtool.resolver;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeFinder;

/** This extension also determines the last node boundary. */
public class ASTNodeFinderExtension extends ASTNodeFinder {
	
	public int lastNodeBoundary = -1;
	
	public ASTNodeFinderExtension(ASTNode root, int offset, boolean inclusiveEnd) {
		super(root, offset, inclusiveEnd, null);
		findNodeInAST();
		assertTrue(lastNodeBoundary >= 0);
	}
	
	@Override
	public boolean preVisit(ASTNode node) {
		if(node.hasSourceRangeInfo() && node.getStartPos() <= offset ) {
			lastNodeBoundary = node.getStartPos();
		}
		return super.preVisit(node);
	}
	
	@Override
	public void postVisit(ASTNode node) {
		if(node.hasSourceRangeInfo() && node.getEndPos() <= offset ) {
			lastNodeBoundary = node.getEndPos();
		}
		super.postVisit(node);
	}
}