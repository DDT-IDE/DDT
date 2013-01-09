package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import dtool.ast.definitions.Module;
import dtool.parser.Token.ErrorToken;
import dtool.tests.CommonTestUtils;

public class DeeParserTest extends CommonTestUtils {
	
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
		
		if(expectedGenSource != null) {
			String generatedSource = module.toStringAsCode();
			checkSourceEquality(generatedSource, expectedGenSource);
		}
		
		if(allowAnyErrors == false) {
			checkParserErrors(result.errors, expectedErrors);
		}
		
		// Check source ranges
		module.accept(new ASTSourceRangeChecker(parseSource, result.errors));
	}
	
	public static void checkSourceEquality(String generatedSource, String expectedGenSource) {
		checkSourceEquality(generatedSource, expectedGenSource, false);
	}
	
	public static void checkSourceEquality(String source, String expectedSource, boolean ignoreUT) {
		DeeLexer generatedSourceLexer = new DeeLexer(source);
		DeeLexer expectedSourceLexer = new DeeLexer(expectedSource);
		
		while(true) {
			Token tok = getContentToken(generatedSourceLexer, true, ignoreUT);
			Token tokExp = getContentToken(expectedSourceLexer, true, ignoreUT);
			assertEquals(tok.type, tokExp.type);
			assertEquals(tok.tokenSource, tokExp.tokenSource);
			
			if(tok.type == DeeTokens.EOF) {
				break;
			}
		}
	}
	
	public static Token getContentToken(DeeLexer lexer, boolean ignoreComments, boolean ignoreUT) {
		while(true) {
			Token token = lexer.next();
			if((token.type.isParserIgnored && (ignoreComments || !isCommentToken(token))) 
				|| (ignoreUT && isUnknownToken(token))) {
				continue;
			}
			return token;
		}
	}
	
	public static boolean isUnknownToken(Token token) {
		if(token instanceof ErrorToken) {
			ErrorToken errorToken = (ErrorToken) token;
			if(errorToken.originalToken == DeeTokens.ERROR) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isCommentToken(Token token) {
		return 
			token.type == DeeTokens.COMMENT_LINE ||
			token.type == DeeTokens.COMMENT_MULTI ||
			token.type == DeeTokens.COMMENT_NESTED;
	}
	
	public static void checkParserErrors(ArrayList<ParserError> resultErrors, ArrayList<ParserError> expectedErrors) {
		for (int i = 0; i < resultErrors.size(); i++) {
			ParserError error = resultErrors.get(i);
			
			assertTrue(i < expectedErrors.size());
			ParserError expError = expectedErrors.get(i);
			assertEquals(error.errorType, expError.errorType);
			assertEquals(error.sourceRange, expError.sourceRange);
			assertEquals(error.msgErrorSource, expError.msgErrorSource);
			assertAreEqual(safeToString(error.msgObj2), safeToString(expError.msgObj2));
		}
		assertTrue(resultErrors.size() == expectedErrors.size());
	}
	
}