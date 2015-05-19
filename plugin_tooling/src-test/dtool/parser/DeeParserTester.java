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

import static dtool.util.NewUtils.replaceRegexFirstOccurrence;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import melnorme.lang.tooling.ast.ASTVisitor;
import melnorme.lang.tooling.ast.ParserError;
import melnorme.lang.tooling.ast.ParserErrorTypes;
import melnorme.lang.tooling.ast.util.ASTSourceRangeChecker;
import melnorme.lang.tooling.ast_actual.ASTNode;
import melnorme.utilbox.collections.ArrayList2;
import melnorme.utilbox.collections.Indexable;
import melnorme.utilbox.misc.MiscUtil;
import melnorme.utilbox.misc.StringUtil;
import melnorme.utilbox.tests.CommonTestUtils;
import dtool.ast.declarations.DeclarationAttrib;
import dtool.ast.declarations.DeclarationAttrib.AttribBodySyntax;
import dtool.ast.definitions.CommonDefinition;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.DefinitionAlias;
import dtool.ast.definitions.DefinitionAlias.DefinitionAliasFragment;
import dtool.ast.definitions.FunctionAttribute;
import dtool.ast.definitions.IFunctionParameter;
import dtool.ast.definitions.ITemplateParameter;
import dtool.ast.definitions.Module;
import dtool.ast.expressions.ExpLiteralBool;
import dtool.ast.expressions.ExpLiteralFloat;
import dtool.ast.expressions.ExpLiteralInteger;
import dtool.ast.expressions.ExpLiteralMapArray.MapArrayLiteralKeyValue;
import dtool.ast.expressions.ExpLiteralString;
import dtool.ast.expressions.ExpPostfixOperator;
import dtool.ast.expressions.ExpReference;
import dtool.ast.references.AutoReference;
import dtool.ast.statements.SimpleVariableDef;
import dtool.ast.statements.StatementExpression;
import dtool.parser.DeeParser_Parameters.AmbiguousParameter;
import dtool.parser.DeeParser_Parameters.TplOrFnMode;
import dtool.parser.DeeParsingChecks.DeeTestsChecksParser;
import dtool.parser.DeeParsingChecks.ParametersReparseCheck;
import dtool.parser.common.AbstractParser.ParseRuleDescription;
import dtool.parser.common.Token;
import dtool.sourcegen.AnnotatedSource.MetadataEntry;
import dtool.tests.DToolTests;
import dtool.util.NewUtils;


public class DeeParserTester extends CommonTestUtils {
	
	public static final String DONT_CHECK = "#DONTCHECK";
	
	protected final String fullSource;
	protected final String parseRule;
	protected final String expectedRemainingSource; 
	protected final String expectedPrintedSource;
	protected final NamedNodeElement[] expectedStructure; 
	protected final ArrayList2<ParserError> expectedErrors;
	protected final List<MetadataEntry> additionalMetadataOriginal;
	protected HashMap<String, Object> additionalMD;

	
	public DeeParserTester(String fullSource, String parseRule, String expectedRemainingSource, 
		String expectedPrintedSource, NamedNodeElement[] expectedStructure, ArrayList2<ParserError> expectedErrors,
		List<MetadataEntry> additionalMetadata) {
		this.fullSource = fullSource;
		this.parseRule = parseRule;
		this.expectedRemainingSource = expectedRemainingSource;
		this.expectedPrintedSource = expectedPrintedSource;
		this.expectedStructure = expectedStructure;
		this.expectedErrors = expectedErrors;
		this.additionalMetadataOriginal = additionalMetadata;
	}
	
	public static HashMap<String, Object> buildMetadataMap(List<MetadataEntry> entryList) {
		HashMap<String, Object> entriesMap = new HashMap<>();
		
		for (MetadataEntry metadataEntry : entryList) {
			String key = metadataEntry.name;
			Object existingEntry = entriesMap.get(key);
			if(existingEntry == null) {
				entriesMap.put(key, metadataEntry);
			} else {
				ArrayList<MetadataEntry> listEntry = null; 
				if(existingEntry instanceof MetadataEntry) {
					listEntry = new ArrayList<>();
					listEntry.add((MetadataEntry) existingEntry);
				} else {
					listEntry = assertCast(existingEntry, ArrayList.class);
				}
				listEntry.add(metadataEntry);
				entriesMap.put(key, listEntry);
			}
		}
		return entriesMap;
	}
	
	public MetadataEntry getTestMetadata(String mdName) {
		return assertCast(additionalMD.get(mdName), MetadataEntry.class);
	}
	
	public MetadataEntry removeTestMetadata(String mdName) {
		return assertCast(additionalMD.remove(mdName), MetadataEntry.class);
	}
	
	public boolean removeTestMetadataFlag(String mdName) {
		return additionalMD.remove(mdName) != null;
	}
	
	public List<MetadataEntry> removeTestMetadataEntries(String mdName) {
		Object entry = additionalMD.remove(mdName);
		if(entry == null) {
			return Collections.EMPTY_LIST;
		} else if(entry instanceof MetadataEntry) {
			return NewUtils.arrayListFromElement((MetadataEntry) entry);
		}
		return assertCast(entry, List.class);
	}
	
	// The funky name here is to help locate this function in stack traces during debugging
	public void runParserTest______________________() {
		additionalMD = buildMetadataMap(additionalMetadataOriginal);
		
		final DeeTestsChecksParser deeParser = new DeeTestsChecksParser(fullSource);
		DeeParserResult result = parseUsingRule(deeParser);
		if(result == null) 
			return;
		
		String parsedSource = checkParsedSource(expectedRemainingSource, deeParser);
		ASTNode mainNode = result.node; // a null result may make sense in some tests
		
		if(mainNode != null) {
			checkBasicStructureContracts(mainNode);
		}
		
		checkExpectedStructure(mainNode, expectedStructure);
		
		if(expectedErrors != null) {
			checkParserErrors(result.getErrors(), expectedErrors);
		}
		
		if(expectedPrintedSource != null) {
			assertTrue(result.errors.size() == 0 ? parsedSource.equals(expectedPrintedSource) : true);
			
			String nodePrintedSource = mainNode == null ? "" : mainNode.toStringAsCode();
			SourceEquivalenceChecker.assertCheck(nodePrintedSource, expectedPrintedSource);
		}
		
		// Check consistency of source ranges (no overlapping ranges)
		if(mainNode != null) {
			testNodeContracts(mainNode);
		}
		
		runAdditionalTests(result, parsedSource);
	}
	
	public static void testNodeContracts(ASTNode mainNode) {
		ASTSourceRangeChecker.checkConsistency(mainNode);
		
		ASTCloneTests.testCloning(mainNode);
	}
	
	public String checkParsedSource(final String expectedRemainingSource, final DeeTestsChecksParser deeParser) {
		String parsedSource = fullSource;
		String remainingSource = fullSource.substring(deeParser.getSourcePosition());
		if(expectedRemainingSource == DeeParserTester.DONT_CHECK) {
			parsedSource = fullSource.substring(0, deeParser.getSourcePosition());
		} else if(expectedRemainingSource == null) {
			assertTrue(deeParser.lookAhead() == DeeTokens.EOF);
		} else {
			SourceEquivalenceChecker.assertCheck(remainingSource, expectedRemainingSource);
			parsedSource = fullSource.substring(0, fullSource.length() - expectedRemainingSource.length());
		}
		return parsedSource;
	}
	
	/* ============= Structure Checkers ============= */
	
	public static void checkBasicStructureContracts(ASTNode parsedNode) {
		assertTrue(parsedNode.getLexicalParent() == null);
		parsedNode.accept(new ASTVisitor() {
			ASTNode parent = null;
			ASTNode lastVisitedNode = null;
			
			@Override
			public boolean preVisit(ASTNode node) {
				assertTrue(node.isSemanticReady());
				assertTrue(node.getLexicalParent() == parent);
				parent = node;
				lastVisitedNode = node;
				return true;
			}
			
			@Override
			public void postVisit(ASTNode node) {
				assertTrue(node.hasChildren() == (node != lastVisitedNode));
				parent = (ASTNode) node.getLexicalParent();
			}
		});
	}
	
	public static class NamedNodeElement {
		public static final String IGNORE_ALL = "*";
		public static final String IGNORE_NAME = "?";
		
		public final String name;
		public final NamedNodeElement[] children;
		
		public NamedNodeElement(String name, NamedNodeElement[] children) {
			this.name = assertNotNull(name);
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
		} if(expectedNameRaw.equals("StatementExp")) {
			return StatementExpression.class.getSimpleName();
		}
		if(expectedNameRaw.equals("FnAttrib")) {
			return FunctionAttribute.class.getSimpleName();
		}
		
		return replaceRegexFirstOccurrence(expectedNameRaw, 
			"(Def)(Variable|AutoVar|Function|Constructor|EnumVarFragment)", 1, "Definition");
	}
	
	/* ============= Error and Source Range Checkers ============= */
	
	public static void checkParserErrors(Indexable<ParserError> _resultErrors, Indexable<ParserError> _expectedErrors) {
		ArrayList2<ParserError> resultErrors = new ArrayList2<>(_resultErrors);
		ArrayList2<ParserError> expectedErrors = new ArrayList2<>(_expectedErrors);
		Collections.sort(resultErrors, new ParserErrorComparator());
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
	
	public static final class ParserErrorComparator implements Comparator<ParserError> {
		@Override
		public int compare(ParserError o1, ParserError o2) {
			int compareResult = o1.sourceRange.compareTo(o2.sourceRange);
			if(compareResult == 0) {
				compareResult = o1.errorType.ordinal() - o2.errorType.ordinal(); 
			}
			if(compareResult == 0) {
				compareResult = NewUtils.compareStrings(o1.msgErrorSource, o2.msgErrorSource); 
			}
			return compareResult;
		}
	}
	
	/* ---------------- Rule specific tests ---------------- */
	
	public DeeParserResult parseUsingRule(DeeTestsChecksParser deeParser) {
		boolean parseAsFnParamOnly = removeTestMetadataFlag("FN_ONLY");
		boolean parseAsTplParamOnly = removeTestMetadataFlag("TPL_ONLY");
		
		if(parseRule == null) {
		} else if(parseRule.equalsIgnoreCase("EXPRESSION_ToE")) {
			return deeParser.parseUsingRule(DeeParser.RULE_EXPRESSION);
		} else if(parseRule.equalsIgnoreCase("PARAMETER_TEST")) {
			
			Object ambigParsedResult = deeParser.new DeeParser_RuleParameters(TplOrFnMode.AMBIG).parseParameter();
			String parsedSource = checkParsedSource(expectedRemainingSource, deeParser);
			parameterTest(parseAsFnParamOnly, parseAsTplParamOnly, parsedSource, ambigParsedResult);
			
			return null;
		}
		ParseRuleDescription parseRuleDesc = getParseRule(parseRule);
		if(parseRuleDesc == null) {
			return deeParser.parseModuleSource(null, MiscUtil.createValidPath("_parser_tests.d"));
		}
		return deeParser.parseUsingRule(parseRuleDesc);
	}
	
	public static ParseRuleDescription getParseRule(String parseRuleName) {
		if(parseRuleName == null) {
			return null;
		} else if(parseRuleName.equalsIgnoreCase(DeeParser.RULE_EXPRESSION.id)) {
			return DeeParser.RULE_EXPRESSION;
		} else if(parseRuleName.equalsIgnoreCase(DeeParser.RULE_REFERENCE.id)) {
			return DeeParser.RULE_REFERENCE;
		} else if(parseRuleName.equalsIgnoreCase(DeeParser.RULE_DECLARATION.id)) {
			return DeeParser.RULE_DECLARATION;
		} else if(parseRuleName.equalsIgnoreCase(DeeParser.RULE_TYPE_OR_EXP.id) 
			|| parseRuleName.equalsIgnoreCase("TypeOrExp")) {
			return DeeParser.RULE_TYPE_OR_EXP;
		} else if(parseRuleName.equalsIgnoreCase(DeeParser.RULE_INITIALIZER.id)) {
			return DeeParser.RULE_INITIALIZER;
		} else if(parseRuleName.equalsIgnoreCase(DeeParser.RULE_STATEMENT.id)) {
			return DeeParser.RULE_STATEMENT;
		} else if(parseRuleName.equalsIgnoreCase("INIT_STRUCT")) {
			return DeeParser.RULE_STRUCT_INITIALIZER;
		}
		throw assertFail();
	}
	
	public void runAdditionalTests(final DeeParserResult result, final String parsedSource) {
		
		boolean ruleBreakExpected = removeTestMetadataEntries("RULE_BROKEN").isEmpty() == false;
		if(removeTestMetadata("IGNORE_BREAK_FLAG_CHECK") == null) {
			assertTrue(result.ruleBroken == ruleBreakExpected);
			if(result.ruleBroken) {
				// if rule syntax is broken then node position must include all pending whitespace source.
				assertTrue(result.node.getEndPos() >= parsedSource.length());
				// The reason the above is not a strict equality check is just because
				// parsedSource/expectedRemainingSource is not entirely accurate in most test cases:
				// there is usually a bit of whitespace in expectedRemainingSource that may be consumed as well.
			}
		}
		
		if(parseRule == null) {
		} 
		else if(parseRule.equalsIgnoreCase("REFERENCE")) {
			if(DToolTests.TESTS_LITE_MODE == false) {
				DeeTestsChecksParser parser = new DeeTestsChecksParser(parsedSource);
				DeeParserResult resultToE = parser.parseUsingRule(DeeParser.RULE_TYPE_OR_EXP);
				DeeParsingChecks.checkNodeEquality(result.node, resultToE.node);
			}
		} 
		else if(parseRule.equalsIgnoreCase("EXPRESSION_ToE")) {
			DeeTestsChecksParser parser = new DeeTestsChecksParser(parsedSource);
			DeeParserResult resultToE = parser.parseUsingRule(DeeParser.RULE_TYPE_OR_EXP);
			ASTNode expNode = result.node;
			Indexable<ParserError> resultToE_Errors = resultToE.getErrors();
			if(result.errors.size() >= 1) {
				ParserError lastError = result.getErrors().get(result.errors.size()-1);
				if(lastError.errorType == ParserErrorTypes.TYPE_USED_AS_EXP_VALUE &&
					SourceEquivalenceChecker.check(result.node.toStringAsCode(), lastError.msgErrorSource)) {
					resultToE_Errors = new ArrayList2<>(resultToE.getErrors()).addElements(lastError);
					expNode = ((ExpReference) expNode).ref;
				}
			}
			if(expNode instanceof ExpReference) {
				expNode = ((ExpReference) expNode).ref;
			}
			DeeParsingChecks.checkNodeEquality(expNode, resultToE.node);
			assertEquals(result.getErrors(), resultToE_Errors);
		}
		
		runDDocTest(result);
		
		assertTrue(additionalMD.isEmpty());
	}
	
	public static void parameterTest(boolean parseAsFnParamOnly, boolean parseAsTplParamOnly, 
		String nodeSource, Object ambigParsedResult) {
		assertTrue(!(parseAsFnParamOnly == true && parseAsTplParamOnly == true));
		
		if(parseAsFnParamOnly) {
			assertTrue(ambigParsedResult instanceof IFunctionParameter);
		} else if(parseAsTplParamOnly) {
			assertTrue(ambigParsedResult instanceof ITemplateParameter);
		} else {
			assertTrue(ambigParsedResult instanceof AmbiguousParameter);
		}
		
		if(!DToolTests.TESTS_LITE_MODE) {
			ParametersReparseCheck.ambigParameterReparseTest(nodeSource);				
		}
	}
	
	public void runDDocTest(final DeeParserResult result) {
		MetadataEntry targetMDE = removeTestMetadata("DDOC_TEST_TARGET");
		if(targetMDE == null) {
			return;
		}
		
		DefUnit defUnit = findDDocTargetDefUnit(result, targetMDE);
		assertNotNull(defUnit);
		List<MetadataEntry> ddocTestEntries = removeTestMetadataEntries("DDOC_TEST");
		
		ArrayList<Token> commentsToCheck = defUnit.getDocComments() == null ? new ArrayList<Token>() : 
			new ArrayList<>(Arrays.asList(defUnit.getDocComments()));
		
		
		Token[] comments = defUnit.getDocComments();
		CommonDefinition commonDefinition = defUnit instanceof CommonDefinition ? (CommonDefinition) defUnit : null;
		
		if(comments != null && comments.length > 0 && !(defUnit instanceof Module) && commonDefinition != null) {
			int extendedStartPos = commonDefinition.getExtendedStartPos();
			int extendedEndPos = commonDefinition.getExtendedEndPos();
			
			if(comments.length == 1) {
				assertTrue(
					comments[0].getEndPos() == extendedEndPos ||
					comments[0].getStartPos() == extendedStartPos);
			} else {
				assertTrue(comments[0].getStartPos() == extendedStartPos);
				int ddocEnd = comments[comments.length-1].getEndPos();
				assertTrue(ddocEnd < defUnit.defName.getStartPos() || ddocEnd == extendedEndPos);
			}
		}
		
		for (MetadataEntry ddocTest : ddocTestEntries) {
			checkDDocComments(commentsToCheck, ddocTest);
		}
		
		assertTrue(commentsToCheck.isEmpty()); // All comment tokens must be tagged.
	}
	
	protected DefUnit findDDocTargetDefUnit(DeeParserResult result, MetadataEntry targetMDE) {
		MetadataEntry targetMDEOverride = removeTestMetadata("DDOC_TEST_TARGET__OVERRIDE");
		if(targetMDE.sourceValue == null) {
			return getDefunitFromExtendedDefinition(result.node);
		}
		if(targetMDEOverride != null) {
			targetMDE = targetMDEOverride;
		}
		
		String defUnitName = targetMDE.sourceValue.trim();
		DefUnit targetDefUnit = null;
		
		for (ASTNode child : result.node.getChildren()) {
			DefUnit defUnit = getDefunitFromExtendedDefinition(child);
			if(defUnit != null) {
				if(defUnit.defName.name.equals(defUnitName)) {
					assertTrue(targetDefUnit == null); // check that there is only one defunit with that name
					targetDefUnit = defUnit;
				}
			}
		}
		return assertNotNull(targetDefUnit);
	}
	
	public DefUnit getDefunitFromExtendedDefinition(ASTNode node) {
		while(true) {
			if(node instanceof DefUnit) {
				return (DefUnit) node;
			}
			if(node instanceof DeclarationAttrib) {
				DeclarationAttrib declAttrib = (DeclarationAttrib) node;
				if(declAttrib.bodySyntax == AttribBodySyntax.SINGLE_DECL) {
					node = declAttrib.body;
					continue;
				}
			}
			if(node instanceof DefinitionAlias) {
				DefinitionAlias definitionAlias = (DefinitionAlias) node;
				return definitionAlias.aliasFragments.get(0); 
			}
			return null;
		}
	}
	
	public void checkDDocComments(ArrayList<Token> comments, MetadataEntry ddocTest) {
		for (Token token : comments) {
			if(token.getSourceValue().equals(ddocTest.sourceValue)) { 
				comments.remove(token);
				return;
			}
		}
		assertFail(); // Must find it in comments
	}
	
}