package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.ArrayList;

import dtool.ast.ASTNeoNode;
import dtool.ast.NodeList2;
import dtool.ast.definitions.Module;
import dtool.parser.DeeParserSourceBasedTest.NamedNodeElement;
import dtool.parser.Token.ErrorToken;
import dtool.tests.CommonTestUtils;

public class DeeParserTest extends CommonTestUtils {
	
	public static void runParserTest______________________(String parseSource, String expectedGenSource, 
		NamedNodeElement[] expectedStructure, ArrayList<ParserError> expectedErrors, boolean allowAnyErrors) {
		
		DeeParserResult result = DeeParser.parse(parseSource);
		
		Module module = result.module;
		assertNotNull(module);
		
		if(expectedGenSource != null) {
			checkSourceEquality(module, expectedGenSource);
		}
		
		if(expectedStructure != null) {
			checkExpectedStructure(module, expectedStructure);
		}
		
		if(allowAnyErrors == false) {
			checkParserErrors(result.errors, expectedErrors);
		}
		if(result.errors.size() == 0) {
			checkSourceEquality(module, parseSource);
		}
		
		// Check source ranges
		module.accept(new ASTSourceRangeChecker(parseSource, result.errors));
	}
	
	public static void checkSourceEquality(ASTNeoNode node, String expectedGenSource) {
		String generatedSource = node.toStringAsCode();
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
	
	public static void checkExpectedStructure(Module module, NamedNodeElement[] expectedStructure) {
		ASTNeoNode[] children = module.getChildren();
		checkExpectedStructure(children, expectedStructure, true);
	}
	
	public static void checkExpectedStructure(ASTNeoNode[] children, NamedNodeElement[] expectedStructure,
		boolean flattenNodeList) {
		
		if(flattenNodeList && children.length == 1 && children[0] instanceof NodeList2) {
			children = children[0].getChildren();
		}
		
		assertTrue(children.length == expectedStructure.length);
		
		for (int i = 0; i < expectedStructure.length; i++) {
			NamedNodeElement namedElement = expectedStructure[i];
			ASTNeoNode astNode = children[i];
			assertEquals(astNode.getClass().getSimpleName(), namedElement.name);
			checkExpectedStructure(astNode.getChildren(), namedElement.children, true);
		}
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