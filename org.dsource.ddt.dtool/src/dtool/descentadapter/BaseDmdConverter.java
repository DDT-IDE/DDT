package dtool.descentadapter;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import descent.internal.compiler.parser.ASTDmdNode;
import dtool.ast.SourceRange;


public class BaseDmdConverter {
	
	public static SourceRange sourceRange(ASTDmdNode node) {
		if (node.getStartPos() == -1) {
			return null;
		}
		return sourceRangeValid(node);
	}
	
	public static SourceRange sourceRangeForced(ASTDmdNode node) {
		return new SourceRange(node.getStartPos(), node.getLength(), false);
	}
	
	public static SourceRange sourceRangeValid(ASTDmdNode node) {
		assertTrue(node.hasNoSourceRangeInfo() == false);
		assertTrue(node.getLength() > 0);
		return new SourceRange(node.getStartPos(), node.getLength());
	}
	
	public static SourceRange sourceRangeValid(int startPos, int endPos) {
		return new SourceRange(startPos, endPos - startPos);
	}
	
}