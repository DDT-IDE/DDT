package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import dtool.parser.common.Token;
import melnorme.utilbox.misc.ArrayUtil;

public class SourceEquivalenceChecker {
	
	public static void assertCheck(String source, String expectedSource, DeeTokens... additionalIgnores) {
		check(source, expectedSource, true, additionalIgnores);
	}
	public static boolean check(String source, String expectedSource) {
		return check(source, expectedSource, false);
	}
	
	public static boolean check(String source, String expectedSource, boolean failOnUnequal, 
		DeeTokens... additionalIgnores) {
		DeeLexer generatedSourceLexer = new DeeLexer(source);
		DeeLexer expectedSourceLexer = new DeeLexer(expectedSource);
		
		while(true) {
			Token tok = getContentToken(generatedSourceLexer, true, additionalIgnores);
			Token tokExp = getContentToken(expectedSourceLexer, true, additionalIgnores);
			if(tok.type.equals(tokExp.type) && tok.source.equals(tokExp.source)) {
			} else if(failOnUnequal) {
				assertFail();
			} else {
				return false;
			}
			
			if(tok.type == DeeTokens.EOF) {
				return true;
			}
		}
	}
	
	public static Token getContentToken(DeeLexer lexer, boolean ignoreComments, DeeTokens... additionalIgnores) {
		while(true) {
			Token token = lexer.next();
			DeeTokens type = token.type;
			if((type.isSubChannel && (type.getGroupingToken() != DeeTokens.GROUP_COMMENT || ignoreComments)) 
				|| (ArrayUtil.contains(additionalIgnores, type)))
				continue;
			return token;
		}
	}
}