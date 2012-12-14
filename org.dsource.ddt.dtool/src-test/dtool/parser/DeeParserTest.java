package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertEquals;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.ArrayList;

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
		runParserTest(testSource, testSource, new ArrayList<ParserError>(), false);
	}
	
	public static void runParserTest(String parseSource, String expectedGenSource, 
		ArrayList<ParserError> expectedErrors, boolean allowAnyErrors) {
		DeeParserResult result = DeeParser.parse(parseSource);
		
		Module module = result.module;
		assertNotNull(module);
		
		ASTSourceRangeChecker.ASTAssertChecker.checkConsistency(module);
		
		String generatedSource = ASTSourcePrinter.printSource(module);
		checkSourceEquality(generatedSource, expectedGenSource);
		
		if(allowAnyErrors == false) {
			checkParserErrors(result.errors, expectedErrors);
		}
	}
	
	public static void checkSourceEquality(String generatedSource, String expectedGenSource) {
		DeeLexer generatedSourceLexer = new DeeLexer(generatedSource);
		DeeLexer expectedSourceLexer = new DeeLexer(expectedGenSource);
		
		while(true) {
			Token tok = getContentToken(generatedSourceLexer, true);
			Token tokExp = getContentToken(expectedSourceLexer, true);
			assertEquals(tok.tokenType, tokExp.tokenType);
			assertEquals(tok.value, tokExp.value);
			
			if(tok.tokenType == DeeTokens.EOF) {
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
	
	public static void checkParserErrors(ArrayList<ParserError> resultErrors, ArrayList<ParserError> expectedErrors) {
		for (int i = 0; i < resultErrors.size(); i++) {
			ParserError error = resultErrors.get(i);
			
			assertTrue(i < expectedErrors.size());
			ParserError expError = expectedErrors.get(i);
			assertEquals(error.errorType, expError.errorType);
			assertEquals(error.sourceRange, expError.sourceRange);
			if(expError.msgErrorSource != null) {
				assertEquals(error.msgErrorSource, expError.msgErrorSource);
			}
			if(expError.msgObj2 != null) {
				assertEquals(error.msgObj2, expError.msgObj2);
			}
		}
		assertTrue(resultErrors.size() == expectedErrors.size());
	}
	
}