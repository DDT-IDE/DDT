package dtool.descentadapter;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import descent.internal.compiler.parser.ASTDmdNode;
import dtool.ast.NeoSourceRange;


public class BaseDmdConverter {
	
	public static NeoSourceRange sourceRange(ASTDmdNode node) {
		if (node.getStartPos() == -1) {
			return null;
		}
		return sourceRangeValid(node);
	}
	
	public static NeoSourceRange sourceRangeForced(ASTDmdNode node) {
		return new NeoSourceRange(node.getStartPos(), node.getLength(), false);
	}
	
	public static NeoSourceRange sourceRangeValid(ASTDmdNode node) {
		assertTrue(node.hasNoSourceRangeInfo() == false);
		assertTrue(node.getLength() > 0);
		return new NeoSourceRange(node.getStartPos(), node.getLength());
	}
	
	public static NeoSourceRange sourceRangeValid(int startPos, int endPos) {
		return new NeoSourceRange(startPos, endPos - startPos);
	}
	
}