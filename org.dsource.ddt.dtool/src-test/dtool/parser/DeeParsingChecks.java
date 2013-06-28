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
import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.CoreUtil.areEqual;

import java.util.ArrayList;

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
		assertEquals(reparsedNode.isParsedStatus(), node.isParsedStatus());
		if(reparsedNode.isParsedStatus()) {
			assertTrue(areEqual(collectNodeErrors(reparsedNode), collectNodeErrors(node)));
		}
	}
	
	public static ArrayList<ParserError> collectNodeErrors(ASTNode node) {
		assertNotNull_(node);
		return DeeParserResult.collectErrors(new ArrayList<ParserError>(), node);
	}
	
	public static class DeeParsingNodeCheck {
		
		protected final String fullSource;
		protected final ASTNode nodeUnderTest;
		protected final String nodeSnippedSource;
		
		public DeeParsingNodeCheck(String source, ASTNode node) {
			this.fullSource = assertNotNull_(source);
			this.nodeUnderTest = assertNotNull_(node);
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
	
}