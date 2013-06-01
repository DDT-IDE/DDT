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
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.ArrayList;

import dtool.ast.ASTCommonSourceRangeChecker.ASTSourceRangeChecker;
import dtool.ast.ASTNode;
import dtool.ast.ASTNodeTypes;
import dtool.ast.NodeUtil;
import dtool.parser.DeeParser_RuleParameters.AmbiguousParameter;
import dtool.parser.DeeParser_RuleParameters.TplOrFnMode;
import dtool.parser.ParserError.ParserErrorTypes;
import dtool.tests.CommonTestUtils;

/**
 * Various checks for invariants in node parsing.
 * These don't require additional test specification to check against.
 */
public class DeeParsingChecks extends CommonTestUtils {
	
	public static final class DeeTestsLexer extends DeeLexer {
		public DeeTestsLexer(String source) {
			super(source);
		}
		
		@Override
		public String toString() {
			return source.substring(0, pos) + "<---parser--->" + source.substring(pos, source.length());
		}
	}
	
	public static class DeeTestsChecksParser extends DeeParser {
		
		public DeeTestsChecksParser(String source) {
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
		
		DeeParsingSourceRangeChecks.runParsingSourceRangeChecks(node, fullSource);
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
	
	public static void checkNodeEquality(ASTNode reparsedNode, ASTNode node) {
		// We check the nodes are semantically equal by comparing the toStringAsCode
		// TODO: use a more accurate equals method?
		assertEquals(reparsedNode.toStringAsCode(), node.toStringAsCode());
	}
	
	
	public static class DeeParsingNodeCheck {
		
		protected final String fullSource;
		protected final ASTNode nodeUnderTest;
		protected final String nodeSnippedSource;
		
		@SuppressWarnings("deprecation")
		public DeeParsingNodeCheck(String source, ASTNode node) {
			this.fullSource = assertNotNull_(source);
			this.nodeUnderTest = assertNotNull_(node);
			this.nodeSnippedSource = fullSource.substring(nodeUnderTest.getStartPos(), nodeUnderTest.getEndPos());
			assertTrue(nodeUnderTest.getNodeType() != ASTNodeTypes.OTHER);
		}
	}
	
	/* ------------------------------------- */
	
	//TODO: add this check to DeeTestsChecksParser ?
	public static class ParametersReparseCheck extends DeeParsingNodeCheck {
		
		public ParametersReparseCheck(String source, ASTNode node) {
			super(source, node);
		}
		
		protected void functionParamReparseCheck() {
			testParameter(true);
		}
		
		protected void templateParamReparseCheck() {
			testParameter(false);
		}
		
		protected void testParameter(boolean isFunction) {
			DeeParser snippedParser = prepParser(nodeSnippedSource);
			
			Object fromAmbig = new DeeParser_RuleParameters(snippedParser, TplOrFnMode.AMBIG).parseParameter();
			boolean isAmbig = false;
			if(fromAmbig instanceof AmbiguousParameter) {
				isAmbig = true;
				AmbiguousParameter ambiguousParameter = (AmbiguousParameter) fromAmbig;
				fromAmbig = isFunction ? 
					ambiguousParameter.convertToFunction() : 
					ambiguousParameter.convertToTemplate(); 
			}
			DeeParsingChecks.checkNodeEquality((ASTNode) fromAmbig, nodeUnderTest);
			snippedParser = prepParser(nodeSnippedSource);
			
			ASTNode paramParsedTheOtherWay = isFunction ? 
				snippedParser.parseTemplateParameter() : (ASTNode) snippedParser.parseFunctionParameter();
			
			boolean hasFullyParsedCorrectly = allSourceParsedCorrectly(snippedParser, paramParsedTheOtherWay);
			
			assertTrue(hasFullyParsedCorrectly ? isAmbig : true);
			if(hasFullyParsedCorrectly) {
				String expectedSource = nodeUnderTest.toStringAsCode();
				SourceEquivalenceChecker.assertCheck(paramParsedTheOtherWay.toStringAsCode(), expectedSource);
			}
		}
		
		public boolean allSourceParsedCorrectly(DeeParser parser, ASTNode resultNode) {
			return parser.lookAhead() == DeeTokens.EOF && resultNode.getData().hasErrors();
		}
		
		public static DeeParser prepParser(String snippedSource) {
			return new DeeParser(new DeeParsingChecks.DeeTestsLexer(snippedSource));
		}
		
	}
	
}