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
import dtool.ast.definitions.DefinitionAlias.DefinitionAliasFragment;
import dtool.ast.definitions.IFunctionParameter;
import dtool.ast.definitions.Module;
import dtool.ast.definitions.TemplateParameter;
import dtool.ast.expressions.ExpLiteralBool;
import dtool.ast.expressions.ExpLiteralFloat;
import dtool.ast.expressions.ExpLiteralInteger;
import dtool.ast.expressions.ExpLiteralMapArray.MapArrayLiteralKeyValue;
import dtool.ast.expressions.ExpLiteralString;
import dtool.ast.expressions.ExpPostfixOperator;
import dtool.ast.expressions.ExpReference;
import dtool.ast.references.AutoReference;
import dtool.ast.statements.SimpleVariableDef;
import dtool.parser.DeeParserResult.ParserErrorComparator;
import dtool.parser.DeeParser_RuleParameters.AmbiguousParameter;
import dtool.parser.DeeParser_RuleParameters.TplOrFnMode;
import dtool.parser.DeeParsingChecks.DeeTestsChecksParser;
import dtool.parser.DeeParsingChecks.ParametersReparseCheck;
import dtool.parser.ParserError.ParserErrorTypes;
import dtool.sourcegen.AnnotatedSource.MetadataEntry;
import dtool.tests.CommonTestUtils;
import dtool.tests.DToolTests;


public class DeeParserTest extends CommonTestUtils {
	
	public static final String DONT_CHECK = "#DONTCHECK";
	
	protected final String fullSource;
	protected String expectedRemainingSource;
	
	public DeeParserTest(String fullSource) {
		this.fullSource = fullSource;
	}
	
	// The funky name here is to help locate this function in stack traces during debugging
	public void runParserTest______________________(
		final String parseRule, final String expectedRemainingSource, 
		final String expectedPrintedSource, final NamedNodeElement[] expectedStructure, final 
		ArrayList<ParserError> expectedErrors, Map<String, MetadataEntry> additionalMetadata) {
		
		this.expectedRemainingSource = expectedRemainingSource;
		
		final DeeTestsChecksParser deeParser = new DeeTestsChecksParser(fullSource);
		DeeParserResult result = parseUsingRule(parseRule, deeParser, additionalMetadata);
		if(result == null) 
			return;
		
		String parsedSource = checkParsedSource(expectedRemainingSource, deeParser);
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
	
	public String checkParsedSource(final String expectedRemainingSource, final DeeTestsChecksParser deeParser) {
		String parsedSource = fullSource;
		if(expectedRemainingSource == DeeParserTest.DONT_CHECK) {
			parsedSource = fullSource.substring(0, deeParser.getLexPosition());
		} else if(expectedRemainingSource == null) {
			assertTrue(deeParser.lookAhead() == DeeTokens.EOF);
		} else {
			String remainingSource = fullSource.substring(deeParser.getLexPosition());
			SourceEquivalenceChecker.assertCheck(remainingSource, expectedRemainingSource);
			parsedSource = fullSource.substring(0, fullSource.length() - expectedRemainingSource.length());
		}
		return parsedSource;
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
	
	
	/* ---------------- Rule specific tests ---------------- */
	
	public DeeParserResult parseUsingRule(String parseRule, DeeTestsChecksParser deeParser, 
		Map<String, MetadataEntry> additionalMD) {
		if(parseRule != null && parseRule.equalsIgnoreCase("EXPRESSION_ToE")) {
			DeeParserResult resultExp = deeParser.parseUsingRule(DeeParser.RULE_EXPRESSION);
			DeeParserResult resultToE = new DeeParser(deeParser.getSource()).parseUsingRule("TypeOrExp");
			ASTNode expNode = resultExp.node;
			if(resultExp.errors.size() >= 1) {
				ParserError lastError = resultExp.errors.get(resultExp.errors.size()-1);
				if(lastError.errorType == ParserErrorTypes.TYPE_USED_AS_EXP_VALUE &&
					SourceEquivalenceChecker.check(resultExp.node.toStringAsCode(), lastError.msgErrorSource)) {
					resultToE.errors.add(lastError);
					expNode = ((ExpReference) expNode).ref;
				}
			}
			DeeParsingChecks.checkNodeEquality(expNode, resultToE.node);
			assertEquals(resultExp.errors, resultToE.errors);
			return resultExp;
		} else if(parseRule != null && parseRule.equalsIgnoreCase("PARAMETER_TEST")) {
			
			boolean parseAsFnParamOnly = additionalMD.get("FN_ONLY") != null;
			boolean parseAsTplParamOnly = additionalMD.get("TPL_ONLY") != null;
			
			Object ambigParsedResult = new DeeParser_RuleParameters(deeParser, TplOrFnMode.AMBIG).parseParameter();
			String parsedSource = checkParsedSource(expectedRemainingSource, deeParser);
			parameterTest(parseAsFnParamOnly, parseAsTplParamOnly, parsedSource, ambigParsedResult);
			
			return null;
		}
		return deeParser.parseUsingRule(parseRule);
	}
	
	public static void runAdditionalTests(final DeeParserResult result, 
		Map<String, MetadataEntry> additionalMetaDataOriginal) {
		Map<String, MetadataEntry> additionalMD = new HashMap<>(additionalMetaDataOriginal);
		
		MetadataEntry ruleBreakTest = additionalMD.remove("RULE_BROKEN");
		if(additionalMD.remove("IGNORE_BREAK_FLAG_CHECK") == null) {
			assertTrue(result.ruleBroken == (ruleBreakTest != null));
		}
		
		for (Entry<String, MetadataEntry> mde : additionalMD.entrySet()) {
			assertEquals(mde.getValue().value, "flag");
		}
	}
	
	public static void parameterTest(boolean parseAsFnParamOnly, boolean parseAsTplParamOnly, 
		String nodeSource, Object ambigParsedResult) {
		assertTrue(!(parseAsFnParamOnly == true && parseAsTplParamOnly == true));
		
		if(parseAsFnParamOnly) {
			assertTrue(ambigParsedResult instanceof IFunctionParameter);
		} else if(parseAsTplParamOnly) {
			assertTrue(ambigParsedResult instanceof TemplateParameter);
		} else {
			assertTrue(ambigParsedResult instanceof AmbiguousParameter);
		}
		
		if(!DToolTests.TESTS_LITE_MODE) {
			ParametersReparseCheck.ambigParameterReparseTest(nodeSource);				
		}
	}
	
}