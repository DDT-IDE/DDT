package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertEquals;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import org.junit.Test;

import dtool.ast.ASTSourceRangeChecker;
import dtool.ast.definitions.Module;

public class DeeParserTest {
	
	@Test
	public void basicTest() throws Exception { basicTest$(); }
	public void basicTest$() throws Exception {
		runParserTest("module pack.foo;");
	}
	
	public static void runParserTest(String testSource) {
		runParserTest(testSource, testSource);
	}
	
	public static void runParserTest(String parseSource, String expectedGenSource) {
		DeeParserResult result = DeeParser.parse(parseSource);
		
		Module module = result.module;
		assertNotNull(module);
		
		ASTSourceRangeChecker.ASTAssertChecker.checkConsistency(module);
		
		String generatedSource = ASTSourcePrinter.printSource(module);
		checkSourceEquality(expectedGenSource, generatedSource);
	}
	
	public static void checkSourceEquality(String source, String generatedSource) {
		DeeLexer originalSourceLexer = new DeeLexer(source);
		DeeLexer generatedSourceLexer = new DeeLexer(generatedSource);
		
		while(true) {
			Token tok1 = getContentToken(originalSourceLexer, true);
			Token tok2 = getContentToken(generatedSourceLexer, true);
			assertEquals(tok1.tokenType, tok2.tokenType);
			assertEquals(tok1.value, tok2.value);
			
			if(tok1.tokenType == DeeTokens.EOF) {
				break;
			}
		}
	}
	
	public static Token getContentToken(DeeLexer lexer, boolean ignoreComments) {
		while(true) {
			Token token = lexer.next(); 
			if(!token.tokenType.isParserIgnored || (!ignoreComments && 
				isCommentToken(token))) {
				return token;
			}
		}
	}
	
	public static boolean isCommentToken(Token token) {
		return 
			token.tokenType == DeeTokens.COMMENT_LINE ||
			token.tokenType == DeeTokens.COMMENT_MULTI ||
			token.tokenType == DeeTokens.COMMENT_NESTED;
	}
	
}