package dtool.descentadapter;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import descent.internal.compiler.parser.ast.IASTNode;
import dtool.ast.SourceRange;


public class BaseDmdConverter {
	
	public static SourceRange sourceRange(IASTNode node) {
		return sourceRange(node, true);
	}
	
	public static SourceRange sourceRange(IASTNode node, boolean requireNonEmpty) {
		if (node.getStartPos() == -1) {
			return null;
		}
		assertTrue(node.getStartPos() >= 0);
		if(requireNonEmpty) {
			assertTrue(node.getLength() > 0);
		} else {
			assertTrue(node.getLength() >= 0);
		}
		return new SourceRange(node.getStartPos(), node.getLength());
	}
	
	public static SourceRange sourceRangeForced(IASTNode node) {
		return new SourceRange(node.getStartPos(), node.getLength());
	}
	
	public static SourceRange sourceRangeStrict(IASTNode node) {
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