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
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import melnorme.utilbox.misc.StringUtil;
import dtool.ast.ASTCommonSourceRangeChecker.ASTSourceRangeChecker;
import dtool.ast.ASTNode;
import dtool.ast.NodeUtil;
import dtool.ast.definitions.DefinitionAlias.DefinitionAliasFragment;
import dtool.ast.definitions.IFunctionParameter;
import dtool.ast.definitions.Module;
import dtool.ast.expressions.ExpLiteralBool;
import dtool.ast.expressions.ExpLiteralFloat;
import dtool.ast.expressions.ExpLiteralInteger;
import dtool.ast.expressions.ExpLiteralMapArray.MapArrayLiteralKeyValue;
import dtool.ast.expressions.ExpLiteralString;
import dtool.ast.expressions.ExpPostfixOperator;
import dtool.ast.references.AutoReference;
import dtool.ast.statements.SimpleVariableDef;
import dtool.parser.DeeParserResult.ParserErrorComparator;
import dtool.parser.DeeParser_RuleParameters.AmbiguousParameter;
import dtool.parser.DeeParser_RuleParameters.TplOrFnMode;
import dtool.parser.ParserError.ParserErrorTypes;
import dtool.sourcegen.AnnotatedSource.MetadataEntry;
import dtool.tests.CommonTestUtils;
import dtool.tests.DToolTests;


public class DeeParserTest extends CommonTestUtils {
	
	public static final String DONT_CHECK = "#DONTCHECK";
	
	protected final String fullSource;
	
	public DeeParserTest(String fullSource) {
		this.fullSource = fullSource;
	}
	
	// The funky name here is to help locate this function in stack traces during debugging
	public void runParserTest______________________(
		final String parseRule, final String expectedRemainingSource, 
		final String expectedPrintedSource, final NamedNodeElement[] expectedStructure, final 
		ArrayList<ParserError> expectedErrors, Map<String, MetadataEntry> additionalMetadata) {
		
		final DeeTestsFullChecksParser deeParser = new DeeTestsFullChecksParser(fullSource);
		String parsedSource = fullSource;
		DeeParserResult result = parseUsingRule(parseRule, deeParser);
		
		if(expectedRemainingSource == DeeParserTest.DONT_CHECK) {
			parsedSource = fullSource.substring(0, deeParser.getLexPosition());
		} else if(expectedRemainingSource == null) {
			assertTrue(deeParser.lookAhead() == DeeTokens.EOF);
		} else {
			String remainingSource = fullSource.substring(deeParser.getLexPosition());
			SourceEquivalenceChecker.assertCheck(remainingSource, expectedRemainingSource);
			parsedSource = fullSource.substring(0, fullSource.length() - expectedRemainingSource.length());
		}
		ASTNode mainNode = result.node; // a null result may make sense in some tests
		
		if(mainNode != null) {
			checkBasicStructureContracts(array(mainNode), null);
		}
		
		checkExpectedStructure(mainNode, expectedStructure);
		
		if(expectedErrors != null) {
			checkParserErrors(result.errors, expectedErrors);
		}
		
		if(expectedPrintedSource != null) {
			assertTrue(result.errors.size() == 0 ? parsedSource.equals(expectedPrintedSource) : true);
			
			String nodePrintedSource = mainNode == null ? "" : mainNode.toStringAsCode();
			SourceEquivalenceChecker.assertCheck(nodePrintedSource, expectedPrintedSource);
		}
		
		// Check consistency of source ranges (no overlapping ranges)
		if(mainNode != null) {
			ASTSourceRangeChecker.checkConsistency(mainNode);
		}
		
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
		protected void nodeConcluded(ASTNode node) {
			checkNodeSourceRange(node, getSource());
			
			// Run additional tests on the node just parsed
			// These might include node/rule specific tests
			runNodeParsingChecks(node, getSource());
		}
	}
	
	
	/* ============= Structure Checkers ============= */
	
	public static void checkBasicStructureContracts(ASTNode[] children, ASTNode parent) {
		for (ASTNode astNode : children) {
			assertTrue(astNode.getParent() == parent);
			assertTrue(astNode.isParsedStatus());
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
	
	public static void checkExpectedStructure(ASTNode node, NamedNodeElement[] expectedStructure) {
		if(expectedStructure == null) {
			return; // Don't check structure
		} else if(expectedStructure.length == 0) {
			assertTrue(node == null);
			return;
		}
		ASTNode[] children = node instanceof Module ? node.getChildren() : array(node);
		checkExpectedStructure_do(children, expectedStructure);
	}
	
	public static void checkExpectedStructure_do(ASTNode[] children, NamedNodeElement[] expectedStructure) {
		
		assertTrue(children.length <= expectedStructure.length);
		
		for(int i = 0; i < children.length; i++) {
			NamedNodeElement namedElement = expectedStructure[i];
			ASTNode astNode = children[i];
			
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
		} else if(expectedNameRaw.equals("AliasFragment")) {
			return DefinitionAliasFragment.class.getSimpleName();
		} else if(expectedNameRaw.equals("AutoRef")) {
			return AutoReference.class.getSimpleName();
		} else if(expectedNameRaw.equals("SimpleVarDef")) {
			return SimpleVariableDef.class.getSimpleName();
		}
		
		return replaceRegexFirstOccurrence(expectedNameRaw, 
			"(Def)(Variable|AutoVar|Function|Constructor)", 1, "Definition");
	}
	
	/* ============= Error and Source Range Checkers ============= */
	
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
		ASTNode topNode = result.node;
		
		// Check consistency of source ranges (no overlapping ranges)
		ASTSourceRangeChecker.checkConsistency(topNode);
	}
	
	public static void checkNodeSourceRange(ASTNode node, final String fullSource) {
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
			protected void handleSourceRangeStartPosBreach(ASTNode elem) {
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
	
	public static void runNodeParsingChecks(ASTNode node, final String fullSource) {
		String nodeSnippedSource = fullSource.substring(node.getStartPos(), node.getEndPos());
		if(!areThereMissingTokenErrorsInNode(node)) {
			SourceEquivalenceChecker.assertCheck(nodeSnippedSource, node.toStringAsCode());
		} else {
			SourceEquivalenceChecker.assertCheck(nodeSnippedSource, node.toStringAsCode(), structuralControlTokens);
		}
		
		// Reparse check can be computationally expensive. They make parsing quadratic on node depth.
		boolean doReparseCheck = DToolTests.TESTS_LITE_MODE && false; // reparse check disabled
		new ASTNodeReparseCheck(fullSource, node).doCheck(doReparseCheck);
	}
	
	protected static boolean areThereMissingTokenErrorsInNode(ASTNode node) {
		ArrayList<ParserError> nodeErrors = DeeParserResult.collectErrors(new ArrayList<ParserError>(), node);
		for(ParserError error : nodeErrors) {
			if(error.errorType == ParserErrorTypes.INVALID_EXTERN_ID || 
				(error.errorType == ParserErrorTypes.EXPECTED_TOKEN && error.msgData != DeeTokens.IDENTIFIER)) {
				return true;
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
	
	public static void runAdditionalTests(final DeeParserResult result, 
		Map<String, MetadataEntry> additionalMetaDataOriginal) {
		Map<String, MetadataEntry> additionalMD = new HashMap<>(additionalMetaDataOriginal);
		
		MetadataEntry fnParamTest = additionalMD.remove("FN_PARAMETER_TEST");
		if(fnParamTest != null) {
			String source = result.source.substring(fnParamTest.offset);
			DeeParser parser = new DeeParser(source);
			Object parameter = new DeeParser_RuleParameters(parser, TplOrFnMode.AMBIG).parseParameter();
			if(additionalMD.remove("FN_ONLY") != null) {
				assertTrue(parameter instanceof IFunctionParameter);
			} else {
				assertTrue(parameter instanceof AmbiguousParameter);
			}
		}
		MetadataEntry ruleBreakTest = additionalMD.remove("RULE_BROKEN");
		if(additionalMD.remove("IGNORE_BREAK_FLAG_CHECK") == null) {
			assertTrue(result.ruleBroken == (ruleBreakTest != null));
		}
		
		for (Entry<String, MetadataEntry> mde : additionalMD.entrySet()) {
			assertEquals(mde.getValue().value, "flag");
		}
	}
	
}