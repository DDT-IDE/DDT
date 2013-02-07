package dtool.parser;

import static dtool.util.NewUtils.assertNotNull_;
import static dtool.util.NewUtils.replaceRegexFirstOccurrence;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.ArrayList;

import melnorme.utilbox.misc.StringUtil;
import dtool.ast.ASTCommonSourceRangeChecker.ASTAssertChecker;
import dtool.ast.ASTNeoNode;
import dtool.ast.NodeList2;
import dtool.ast.definitions.Module;
import dtool.ast.expressions.ExpLiteralBool;
import dtool.ast.expressions.ExpLiteralFloat;
import dtool.ast.expressions.ExpLiteralInteger;
import dtool.ast.expressions.ExpLiteralString;
import dtool.tests.CommonTestUtils;


public class DeeParserTest extends CommonTestUtils {
	
	public static final String DONT_CHECK = "#DONTCHECK";
	
	public static DeeParserResult parse(String source, String parseRule) {
		DeeParser deeParser = new DeeParser(source);
		if(parseRule == null) {
			return new DeeParserResult(deeParser.parseModule(), deeParser.errors);
		} else if(parseRule.equals("EXPRESSION")){
			DeeParserResult deeParserResult = new DeeParserResult(deeParser.parseExpression(), deeParser.errors);
			assertTrue(deeParser.lookAhead() == DeeTokens.EOF);
			return deeParserResult;
		} else {
			throw assertFail();
		}
	}
	
	public static void runParserTest______________________(
		String parseSource, String parseRule, String expectedGenSource, 
		NamedNodeElement[] expectedStructure, ArrayList<ParserError> expectedErrors, boolean allowAnyErrors) {
		
		DeeParserResult result = parse(parseSource, parseRule);
		
		ASTNeoNode mainNode = result.node;
		assertNotNull(mainNode);
		
		if(expectedStructure != null) {
			checkExpectedStructure(mainNode, expectedStructure);
		}
		
		if(expectedGenSource != null) {
			checkSourceEquality(mainNode, expectedGenSource);
		}
		
		if(result.errors.size() == 0) {
			assertTrue(expectedErrors.size() == 0);
			checkSourceEquality(mainNode, parseSource);
		} else if(allowAnyErrors == false) {
			checkParserErrors(result.errors, expectedErrors);
		}
		
		checkSourceRanges(parseSource, result);
	}
	
	public static class NamedNodeElement {
		public static final String IGNORE_ALL = "*"; 
		public static final String IGNORE_NAME = "?";
		
		public final String name;
		public final NamedNodeElement[] children;
		
		public NamedNodeElement(String name, NamedNodeElement[] children) {
			this.name = assertNotNull_(name);
			this.children = children;
		}
		
		@Override
		public String toString() {
			boolean hasChildren = children != null && children.length > 0;
			return name + (hasChildren ? "("+StringUtil.collToString(children, " ")+")" : "");
		}
	}
	
	public static void checkExpectedStructure(ASTNeoNode parent, NamedNodeElement[] expectedStructure) {
		ASTNeoNode[] children;
		if(parent instanceof Module) {
			children = parent.getChildren();
		} else {
			children = array(parent);
			parent = null;
		}
		checkExpectedStructure(children, parent, expectedStructure, true);
	}
	
	public static void checkExpectedStructure(ASTNeoNode[] children, ASTNeoNode parent, 
		NamedNodeElement[] expectedStructure, boolean flattenNodeList) {
		
		if(flattenNodeList && children.length == 1 && children[0] instanceof NodeList2) {
			parent = children[0];
			children = parent.getChildren();
		}
		
		assertTrue(children.length == expectedStructure.length);
		
		for (int i = 0; i < expectedStructure.length; i++) {
			NamedNodeElement namedElement = expectedStructure[i];
			ASTNeoNode astNode = children[i];
			assertTrue(astNode.getParent() == parent);
			
			if(namedElement.name == NamedNodeElement.IGNORE_ALL) {
				continue;
			}
			if(namedElement.name != NamedNodeElement.IGNORE_NAME) {
				String expectedName = getExpectedNameAliases(namedElement.name);
				assertEquals(astNode.getClass().getSimpleName(), expectedName);
			}
			checkExpectedStructure(astNode.getChildren(), astNode, namedElement.children, true);
		}
	}
	
	public static String getExpectedNameAliases(String expectedNameRaw) {
		if(expectedNameRaw.equals("Bool")) {
			return ExpLiteralBool.class.getSimpleName();
		} else if(expectedNameRaw.equals("Integer")) {
			return ExpLiteralInteger.class.getSimpleName();
		} else if(expectedNameRaw.equals("Float")) {
			return ExpLiteralFloat.class.getSimpleName();
		} else if(expectedNameRaw.equals("String")) {
			return ExpLiteralString.class.getSimpleName();
		} 
		
		return replaceRegexFirstOccurrence(expectedNameRaw, "(Def)(Var)", 1, "Definition");
	}
	
	public static void checkSourceEquality(ASTNeoNode node, String expectedGenSource) {
		String generatedSource = node.toStringAsCode();
		CheckSourceEquality.check(generatedSource, expectedGenSource, false);
	}
	
	public static class CheckSourceEquality {
		
		public static void check(String source, String expectedSource, boolean ignoreUT) {
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
				if(token.type.isParserIgnored 
					&& (ignoreComments || !(token.type.getGroupingToken() == DeeTokens.COMMENT)
					&& (ignoreUT || !(token.type == DeeTokens.INVALID_TOKEN))) 
					) {
					continue;
				}
				return token;
			}
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
			if(expError.msgData != DONT_CHECK) {
				assertAreEqual(safeToString(error.msgData), safeToString(expError.msgData));
			}
		}
		assertTrue(resultErrors.size() == expectedErrors.size());
	}
	
	public static void checkSourceRanges(String parseSource, DeeParserResult result) {
		ASTNeoNode node = result.node;
		
		// Check of source ranges
		node.accept(new ASTSourceRangeChecker(parseSource, result.errors));
		// Next one should not fail if previous one passed.
		ASTAssertChecker.checkConsistency(node);
	}
	
}