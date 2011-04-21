package dtool.descentadapter;

import descent.internal.compiler.parser.ASTDmdNode;
import dtool.ast.NeoSourceRange;


public class BaseDmdConverter {
	
	public static NeoSourceRange sourceRange(ASTDmdNode node) {
		if (node.getStartPos() == -1 || node.getStartPos() == node.getEndPos()) {
			return null; // TODO: source range
		}
		return sourceRangeValid(node);
	}
	
	public static NeoSourceRange sourceRangeValid(ASTDmdNode node) {
		return new NeoSourceRange(node.getStartPos(), node.getEndPos() - node.getStartPos());
	}
	
	public static NeoSourceRange sourceRangeValid(int startPos, int endPos) {
		return new NeoSourceRange(startPos, endPos - startPos);
	}
	
}