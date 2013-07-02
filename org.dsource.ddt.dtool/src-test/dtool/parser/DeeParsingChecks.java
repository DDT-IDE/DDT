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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.CoreUtil.areEqual;

import java.util.ArrayList;

import dtool.ast.ASTHomogenousVisitor;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.ASTSourceRangeChecker;
import dtool.ast.IASTNode;
import dtool.ast.definitions.IFunctionParameter;
import dtool.ast.definitions.TemplateParameter;
import dtool.parser.DeeParser_RuleParameters.AmbiguousParameter;
import dtool.parser.DeeParser_RuleParameters.TplOrFnMode;
import dtool.parser.ParserError.ParserErrorTypes;
import dtool.tests.CommonTestUtils;

/**
 * Various checks for invariants in node parsing.
 * These don't require additional test specification to check against.
 */
public class DeeParsingChecks extends CommonTestUtils {
	
	public static class DeeTestsChecksParser extends DeeParser {
		
		public DeeTestsChecksParser(String source) {
			super(new DeeLexer(source));
		}
		
		@Override
		protected void nodeConcluded(ASTNode node) {
			assertTrue(node.isParsedStatus());
			checkNodeSourceRange(node, getSource());
			
			// Run additional tests on the node just parsed
			// These might include node/rule specific tests
			runNodeParsingChecks(node, getSource());
		}
		
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
			
			@Override
			protected void handleSourceRangeStartPosBreach(ASTNode elem) {
				//String nodeStr = NodeUtil.getSubString(fullSource, elem.getSourceRange());
				//String parentStr = NodeUtil.getSubString(fullSource, elem.getParent().getSourceRange());
				assertFail();
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
		
		DeeParsingSourceRangeChecks.runParsingSourceRangeChecks(node, fullSource);
	}
	
	protected static boolean areThereMissingTokenErrorsInNode(ASTNode node) {
		CheckForMissingTokenErrors visitor = new CheckForMissingTokenErrors();
		node.accept(visitor);
		return visitor.hasMissingTokenErrors;
	}
	
	protected static final class CheckForMissingTokenErrors extends ASTHomogenousVisitor {
		public boolean hasMissingTokenErrors = false;
		
		@Override
		public boolean preVisit(ASTNode node) {
			for (ParserError error : node.getData().getNodeErrors()) {
				if(error.errorType == ParserErrorTypes.INVALID_EXTERN_ID || 
					(error.errorType == ParserErrorTypes.EXPECTED_TOKEN && error.msgData != DeeTokens.IDENTIFIER)) {
					hasMissingTokenErrors = true;
					return DONT_VISIT_CHILDREN;
				}
			}
			return VISIT_CHILDREN;
		}
	}
	
	public static void checkNodeEquality(ASTNode reparsedNode, ASTNode node) {
		// We check the nodes are semantically equal by comparing the toStringAsCode
		// TODO: use a more accurate equals method?
		assertEquals(reparsedNode.getClass(), node.getClass());
		assertEquals(reparsedNode.toStringAsCode(), node.toStringAsCode());
		assertEquals(reparsedNode.isParsedStatus(), node.isParsedStatus());
		if(reparsedNode.isParsedStatus()) {
			assertTrue(areEqual(collectNodeErrors(reparsedNode), collectNodeErrors(node)));
		}
	}
	
	public static ArrayList<ParserError> collectNodeErrors(ASTNode node) {
		assertNotNull(node);
		return DeeParserResult.collectErrors(new ArrayList<ParserError>(), node);
	}
	
	public static class DeeParsingNodeCheck {
		
		protected final String fullSource;
		protected final ASTNode nodeUnderTest;
		protected final String nodeSnippedSource;
		
		public DeeParsingNodeCheck(String source, ASTNode node) {
			this.fullSource = assertNotNull(source);
			this.nodeUnderTest = assertNotNull(node);
			this.nodeSnippedSource = fullSource.substring(nodeUnderTest.getStartPos(), nodeUnderTest.getEndPos());
			assertTrue(nodeUnderTest.getNodeType() != ASTNodeTypes.NULL);
		}
	}
	
	/* ------------------------------------- */
	
	public static class ParametersReparseCheck {
		
		public static Object parseAmbigParameter(String nodeSnippedSource) {
			DeeParser deeParser = new DeeParser(nodeSnippedSource);
			return new DeeParser_RuleParameters(deeParser, TplOrFnMode.AMBIG).parseParameter();
		}
		
		public static void ambigParameterReparseTest(String nodeSource) {
			paramReparseCheck(nodeSource, true);
			paramReparseCheck(nodeSource, false);
		}
		
		public static void paramReparseCheck(String nodeSource, boolean reparseAsFunctionParam) {
			DeeParser unambigParser = new DeeParser(nodeSource);
			IASTNode unambigParsedParameter = reparseAsFunctionParam ? 
				unambigParser.parseFunctionParameter() : unambigParser.parseTemplateParameter();
				
			Object ambigParsedParameterResult = parseAmbigParameter(nodeSource);
			
			ASTNode nodeToCompareAgainst = null;
			if(ambigParsedParameterResult instanceof IFunctionParameter) {
				nodeToCompareAgainst = reparseAsFunctionParam ? (ASTNode) ambigParsedParameterResult : null;
			} else if(ambigParsedParameterResult instanceof TemplateParameter) {
				nodeToCompareAgainst = !reparseAsFunctionParam ? (ASTNode) ambigParsedParameterResult : null;
			} else {
				AmbiguousParameter ambigParsedParameter = (AmbiguousParameter) ambigParsedParameterResult;
				nodeToCompareAgainst = reparseAsFunctionParam ? 
					ambigParsedParameter.convertToFunction().asNode() : ambigParsedParameter.convertToTemplate(); 
			}
			
			if(nodeToCompareAgainst != null) {
				assertTrue(unambigParser.lookAhead() == DeeTokens.EOF);
				DeeParsingChecks.checkNodeEquality(unambigParsedParameter.asNode(), nodeToCompareAgainst);
			} else {
				assertTrue(unambigParser.lookAhead() != DeeTokens.EOF || 
					collectNodeErrors(unambigParsedParameter.asNode()).size() > 0);
			}
		}
		
	}
	
	/* ------------------------------------- */
	
	public static DeeParserResult runSimpleSourceParseTest(String source, String defaultModuleName,
			Boolean expectErrors, boolean runBasicContractChecks) {
		
		DeeParserResult parseResult = runBasicContractChecks ? 
			new DeeTestsChecksParser(source).parseUsingRule(null, defaultModuleName) :
			new DeeParser(source).parseUsingRule(null, defaultModuleName);
		
		if(expectErrors != null) {
			assertTrue(parseResult.hasSyntaxErrors() == expectErrors, "expectedErrors is not: " + expectErrors);
//			source.substring(parseResult.errors.get(0).getStartPos() - 30);
//			source.substring(parseResult.errors.get(0).getStartPos());
		}
		
		return parseResult;
	}
	
}