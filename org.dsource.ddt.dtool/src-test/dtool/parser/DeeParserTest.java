/*******************************************************************************
 * Copyright (c) 2013, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.parser;

import static dtool.util.NewUtils.assertNotNull_;
import static dtool.util.NewUtils.replaceRegexFirstOccurrence;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import melnorme.utilbox.misc.StringUtil;
import dtool.ast.ASTCommonSourceRangeChecker.ASTSourceRangeChecker;
import dtool.ast.ASTNeoNode;
import dtool.ast.ASTSemantics;
import dtool.ast.NodeUtil;
import dtool.ast.definitions.IFunctionParameter;
import dtool.ast.definitions.Module;
import dtool.ast.expressions.ExpLiteralBool;
import dtool.ast.expressions.ExpLiteralFloat;
import dtool.ast.expressions.ExpLiteralInteger;
import dtool.ast.expressions.ExpLiteralMapArray.MapArrayLiteralKeyValue;
import dtool.ast.expressions.ExpLiteralString;
import dtool.ast.expressions.ExpPostfixOperator;
import dtool.parser.DeeParserResult.ParserErrorComparator;
import dtool.parser.DeeParser_RuleParameters.AmbiguousParameter;
import dtool.parser.DeeParser_RuleParameters.TplOrFnMode;
import dtool.parser.ParserError.ParserErrorTypes;
import dtool.sourcegen.AnnotatedSource.MetadataEntry;
import dtool.tests.CommonTestUtils;


public class DeeParserTest extends CommonTestUtils {
	
	protected final String fullSource;
	
	public DeeParserTest(String fullSource) {
		this.fullSource = fullSource;
	}
	
	// The funky name here is to help locate this function in stack traces during debugging
	public void runParserTest______________________(
		final String parseRule, final String expectedRemainingSource, 
		final String expectedPrintedSource, final NamedNodeElement[] expectedStructure, final 
		ArrayList<ParserError> expectedErrors, HashMap<String, MetadataEntry> additionalMetadata) {
		
		final DeeTestsFullChecksParser deeParser = new DeeTestsFullChecksParser(fullSource);
		String parsedSource = fullSource;
		DeeParserResult result = parseUsingRule(parseRule, deeParser);
		
		if(expectedRemainingSource == null) {
			assertTrue(deeParser.lookAhead() == DeeTokens.EOF);
		} else {
			String remainingSource = fullSource.substring(deeParser.getLexPosition());
			SourceEquivalenceChecker.assertCheck(remainingSource, expectedRemainingSource);
			parsedSource = fullSource.substring(0, fullSource.length() - expectedRemainingSource.length());
		}
		ASTNeoNode mainNode = assertNotNull_(result.node);
		
		checkBasicStructureContracts(array(mainNode), null);
		
		if(expectedStructure != null) {
			checkExpectedStructure(mainNode, expectedStructure);
		}
		
		if(expectedErrors != null) {
			checkParserErrors(result.errors, expectedErrors);
		}
		
		assertTrue(result.errors.size() == 0 ? parsedSource.equals(expectedPrintedSource) : true);
		if(expectedPrintedSource != null) {
			SourceEquivalenceChecker.assertCheck(mainNode.toStringAsCode(), expectedPrintedSource);
		}
		
		// Check consistency of source ranges (no overlapping ranges)
		ASTSourceRangeChecker.checkConsistency(mainNode);
		
		runAdditionalTests(result, additionalMetadata);
	}
	
	public static final class DeeTestsLexer extends DeeLexer {
		public DeeTestsLexer(String source) {
			super(source);
		}
		
		@Override
		public String toString() {
			return source.substring(0, pos) + "<---parser--->" + source.substring(pos, source.length());
		}
	}
	
	public static class DeeTestsFullChecksParser extends DeeParser {
		public DeeTestsFullChecksParser(String source) {
			super(new DeeTestsLexer(source));
		}
		
		@Override
		protected <T extends ASTNeoNode> T connect(T node) {
			super.connect(node);
			checkNodeSourceRange(node, getSource());
			
			// Run additional tests on the node just parsed
			// These might include node/rule specific tests
			runNodeParsingChecks(node, getSource(), errors);
			return node;
		}
	}
	
	
	/* ============= Structure Checkers ============= */
	
	public static void checkBasicStructureContracts(ASTNeoNode[] children, ASTNeoNode parent) {
		for (ASTNeoNode astNode : children) {
			assertTrue(astNode.getParent() == parent);
			assertTrue(astNode.getData() == ASTSemantics.PARSED_STATUS);
			checkBasicStructureContracts(astNode.getChildren(), astNode);
		}
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
			return name + (hasChildren ? "(" + StringUtil.collToString(children, " ") + ")" : "");
		}
	}
	
	public static void checkExpectedStructure(ASTNeoNode node, NamedNodeElement[] expectedStructure) {
		ASTNeoNode[] children;
		if(node instanceof Module) {
			children = node.getChildren();
		} else {
			children = array(node);
			node = null;
		}
		checkExpectedStructure_do(children, expectedStructure);
	}
	
	public static void checkExpectedStructure_do(ASTNeoNode[] children, NamedNodeElement[] expectedStructure) {
		
		assertTrue(children.length <= expectedStructure.length);
		
		for(int i = 0; i < children.length; i++) {
			NamedNodeElement namedElement = expectedStructure[i];
			ASTNeoNode astNode = children[i];
			
			if(namedElement.name == NamedNodeElement.IGNORE_ALL) {
				continue;
			}
			if(namedElement.name != NamedNodeElement.IGNORE_NAME) {
				String expectedName = getExpectedNameAliases(namedElement.name);
				assertEquals(astNode.getClass().getSimpleName(), expectedName);
			}
			checkExpectedStructure_do(astNode.getChildren(), namedElement.children);
		}
		assertTrue(children.length == expectedStructure.length);
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
		} else if(expectedNameRaw.equals("MapEntry")) {
			return MapArrayLiteralKeyValue.class.getSimpleName();
		} else if(expectedNameRaw.equals("ExpPostfix") || expectedNameRaw.equals("ExpPostfixOp")) {
			return ExpPostfixOperator.class.getSimpleName();
		}
		
		return replaceRegexFirstOccurrence(expectedNameRaw, "(Def)(Var|AutoVar|Function)", 1, "Definition");
	}
	
	/* ============= Error and Source Range Checkers ============= */
	
	public static final String DONT_CHECK = "#DONTCHECK";
	
	public static void checkParserErrors(ArrayList<ParserError> resultErrors, ArrayList<ParserError> expectedErrors) {
		Collections.sort(expectedErrors, new ParserErrorComparator());
		
		for(int i = 0; i < resultErrors.size(); i++) {
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
	
	public static void checkNodeTreeSourceRanges(final DeeParserResult result) {
		ASTNeoNode topNode = result.node;
		
		// Check consistency of source ranges (no overlapping ranges)
		ASTSourceRangeChecker.checkConsistency(topNode);
	}
	
	public static void checkNodeSourceRange(ASTNeoNode node, final String fullSource) {
		assertTrue(node.hasSourceRangeInfo());
		assertTrue(node.getStartPos() <= fullSource.length() && node.getEndPos() <= fullSource.length());
		
		// Check consistency of source ranges (no overlapping ranges)
		new ASTSourceRangeChecker(node) {
			@Override
			public boolean visitChildrenAfterPreVisitOk() {
				return depth < 2;
			}
			
			@SuppressWarnings("unused")
			@Override
			protected void handleSourceRangeStartPosBreach(ASTNeoNode elem) {
				String nodeStr = NodeUtil.getSubString(fullSource, elem.getSourceRange());
				String parentStr = NodeUtil.getSubString(fullSource, elem.getParent().getSourceRange());
				super.handleSourceRangeStartPosBreach(elem);
				return;
			}
		};
	}
	
	public static final DeeTokens[] structuralControlTokens = array(
		DeeTokens.OPEN_PARENS, DeeTokens.CLOSE_PARENS,
		DeeTokens.OPEN_BRACE, DeeTokens.CLOSE_BRACE,
		DeeTokens.OPEN_BRACKET, DeeTokens.CLOSE_BRACKET,
		DeeTokens.COLON,
		DeeTokens.SEMICOLON
		);
	
	// These checks can be computationally expensive. They make parsing quadratic on node depth.
	public static void runNodeParsingChecks(ASTNeoNode node, final String fullSource, List<ParserError> errors) {
		String nodeSnippedSource = fullSource.substring(node.getStartPos(), node.getEndPos());
		if(!areThereMissingTokenErrorsInNode(node, errors)) {
			SourceEquivalenceChecker.assertCheck(nodeSnippedSource, node.toStringAsCode());
		} else {
			SourceEquivalenceChecker.assertCheck(nodeSnippedSource, node.toStringAsCode(), structuralControlTokens);
		}
		
		new ASTNodeReparseCheck(fullSource, node).doCheck();
	}
	
	protected static boolean areThereMissingTokenErrorsInNode(ASTNeoNode node, Collection<ParserError> errors) {
		for(ParserError error : errors) {
			
			if(!(error.errorType == ParserErrorTypes.INVALID_EXTERN_ID || 
				(error.errorType == ParserErrorTypes.EXPECTED_TOKEN && error.msgData != DeeTokens.IDENTIFIER))) {
				continue;
			}
			
			assertNotNull(error.originNode);
			
			if(NodeUtil.isContainedIn(error.originNode, node)) {
				return true;
			}
			if(error.sourceRange.getStartPos() >= node.getEndPos()) {
				break; // No point in search remaining errors
			}
		}
		return false;
	}
	
	/* ---------------- Rule specific tests ---------------- */
	
	public static DeeParserResult parseUsingRule(final String parseRule, DeeTestsFullChecksParser deeParser) {
		if(parseRule != null && parseRule.equalsIgnoreCase("EXPRESSION_ToE")) {
			DeeParserResult result = deeParser.parseUsingRule(DeeParser.RULE_EXPRESSION);
			DeeParserResult result2 = parseRule.equalsIgnoreCase("EXPRESSION_ToE") ?
				new DeeParser(deeParser.getSource()).parseUsingRule("TypeOrExp") :
				new DeeParser(deeParser.getSource()).parseUsingRule("ExpOrType");
			if(result.errors.size() >= 1) {
				ParserError lastError = result.errors.get(result.errors.size()-1);
				if(lastError.errorType == ParserErrorTypes.TYPE_USED_AS_EXP_VALUE &&
					SourceEquivalenceChecker.check(result.node.toStringAsCode(), lastError.msgErrorSource)) {
					result2.errors.add(lastError);
				}
			}
			ASTNodeReparseCheck.checkNodeEquality(result.node, result2.node);
			assertEquals(result.errors, result2.errors);
			return result;
		}
		return deeParser.parseUsingRule(parseRule);
	}
	
	public static void runAdditionalTests(DeeParserResult result, HashMap<String, MetadataEntry> additionalMetadata) {
		MetadataEntry fnParamTest = additionalMetadata.remove("FN_PARAMETER_TEST");
		if(fnParamTest != null) {
			String source = result.source.substring(fnParamTest.offset);
			DeeParser parser = new DeeParser(source);
			Object parameter = new DeeParser_RuleParameters(parser, TplOrFnMode.AMBIG).parseParameter();
			if(additionalMetadata.remove("FN_ONLY") != null) {
				assertTrue(parameter instanceof IFunctionParameter);
			} else {
				assertTrue(parameter instanceof AmbiguousParameter);
			}
		}
		MetadataEntry ruleBreakTest = additionalMetadata.remove("RULE_BROKEN");
		if(additionalMetadata.remove("IGNORE_BREAK_TEST") == null) {
			assertTrue(result.ruleBroken == (ruleBreakTest != null));
		}
		
		for (Entry<String, MetadataEntry> mde : additionalMetadata.entrySet()) {
			assertEquals(mde.getValue().value, "flag");
		}
	}
	
}