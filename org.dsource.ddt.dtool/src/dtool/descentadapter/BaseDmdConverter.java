package dtool.descentadapter;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import descent.internal.compiler.parser.ASTDmdNode;
import dtool.ast.SourceRange;


public class BaseDmdConverter {
	
	public static SourceRange sourceRange(ASTDmdNode node) {
		if (node.getStartPos() == -1) {
			return null;
		}
		return sourceRangeStrict(node);
	}
	
	public static SourceRange sourceRangeForced(ASTDmdNode node) {
		return new SourceRange(node.getStartPos(), node.getLength());
	}
	
	public static SourceRange sourceRangeStrict(ASTDmdNode node) {
		assertTrue(node.getStartPos() >= 0);
		assertTrue(node.getLength() > 0);
		return new SourceRange(node.getStartPos(), node.getLength());
	}
	
	public static SourceRange sourceRangeStrict(int startPos, int endPos) {
		assertTrue(startPos >= 0);
		int length = endPos - startPos;
		assertTrue(length > 0);
		return new SourceRange(startPos, length);
	}
	
}