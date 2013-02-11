package dtool.parser;

import static dtool.util.NewUtils.assertNotNull_;
import static dtool.util.NewUtils.replaceRegexFirstOccurrence;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import melnorme.utilbox.misc.StringUtil;
import dtool.ast.ASTCommonSourceRangeChecker.ASTSourceRangeChecker;
import dtool.ast.ASTNeoHomogenousVisitor;
import dtool.ast.ASTNeoNode;
import dtool.ast.NodeList2;
import dtool.ast.NodeUtil;
import dtool.ast.definitions.DefSymbol;
import dtool.ast.definitions.Module;
import dtool.ast.expressions.ExpLiteralBool;
import dtool.ast.expressions.ExpLiteralFloat;
import dtool.ast.expressions.ExpLiteralInteger;
import dtool.ast.expressions.ExpLiteralString;
import dtool.ast.expressions.MissingExpression;
import dtool.ast.references.RefIdentifier;
import dtool.ast.references.RefModule;
import dtool.tests.CommonTestUtils;


public class DeeParserTest extends CommonTestUtils {
	
	public static final String DONT_CHECK = "#DONTCHECK";
	
	public static DeeParserResult parse(String source, String parseRule) {
		DeeParser deeParser = new DeeTestsParser(source);
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
		
		checkNodeTreeSourceRanges(result, parseSource);
	}
	
	protected static final class DeeTestsParser extends DeeParser {
		private DeeTestsParser(String source) {
			super(source);
		}
		
		@Override
		protected <T extends ASTNeoNode> T connect(T node) {
			super.connect(node);
			checkNodeSourceRange(node, getSource(), errors);
			return node;
		}
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
				if(!token.type.isParserIgnored 
					|| (!ignoreComments && token.type.getGroupingToken() == DeeTokens.COMMENT)
					|| (!ignoreUT && token.type == DeeTokens.INVALID_TOKEN) 
				) {
					return token;
				}  
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
	
	public static void checkNodeTreeSourceRanges(final DeeParserResult result, final String source) {
		ASTNeoNode topNode = result.node;
		
		// Check consistency of source ranges (no overlapping ranges)
		ASTSourceRangeChecker.checkConsistency(topNode);
		
		new ASTNeoHomogenousVisitor() {
			@Override
			public void genericVisit(ASTNeoNode node) {
				checkNodeSourceRange(node, source, result.errors);
			};
		}.traverse(topNode);
	}
	
	public static void checkNodeSourceRange(ASTNeoNode node, final String fullSource, Collection<ParserError> errors) {
		assertTrue(node.hasSourceRangeInfo());
		assertTrue(node.getStartPos() <= fullSource.length() && node.getEndPos() <= fullSource.length());
		
		// Check consistency of source ranges (no overlapping ranges)
		new ASTSourceRangeChecker(node) {
			@Override
			public boolean visitChildrenAfterPreVisitOk() {
				return depth < 2;
			}
		};
		
		// These checks can be computationally expensive. They make parsing quadratic on node depth.
		if(!areThereMissingTokenErrorsInNode(node, errors)) {
			String nodeSnippedSource = fullSource.substring(node.getStartPos(), node.getEndPos());
			DeeParserTest.CheckSourceEquality.check(nodeSnippedSource, node.toStringAsCode(), true);
		}
		ASTReparseCheckSwitcher.check(node, fullSource, errors);
	}
	
	public static boolean isLeafNodeThatCanBeEmpty(ASTNeoNode node) {
		return node instanceof MissingExpression || node instanceof DefSymbol
			|| node instanceof RefIdentifier  || node instanceof RefModule;
	}
	
	protected static boolean areThereMissingTokenErrorsInNode(ASTNeoNode node, Collection<ParserError> errors) {
		for (ParserError error : errors) {
			
			switch (error.errorType) {
			case EXPECTED_TOKEN: if(error.msgData != DeeTokens.IDENTIFIER) break;
			case MALFORMED_TOKEN:
			case INVALID_TOKEN_CHARACTERS:
			case SYNTAX_ERROR:
			case EXPECTED_RULE:
			case EXP_MUST_HAVE_PARENTHESES:
				continue;
			case INVALID_EXTERN_ID: break;
			}
			
			// Then there is an EXPECTED_TOKEN error in error.originNode
			assertNotNull(error.originNode);
			
			if(NodeUtil.isContainedIn(error.originNode, node)) {
				return true;
			}
			if(error.sourceRange.getStartPos() >= node.getEndPos()) {
				return false;
			}
		}
		return false;
	}
	
	/* ============= Structure Checkers ============= */
	
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
	
}