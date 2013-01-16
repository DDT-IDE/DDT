/*******************************************************************************
 * Copyright (c) 2012, 2012 IBM Corporation and others.
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import melnorme.utilbox.misc.StringUtil;

import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import dtool.ast.SourceRange;
import dtool.parser.ParserError.EDeeParserErrors;
import dtool.sourcegen.AnnotatedSource;
import dtool.sourcegen.AnnotatedSource.MetadataEntry;
import dtool.tests.DToolTestResources;
import dtool.util.NewUtils;

@RunWith(Parameterized.class)
public class DeeParserSourceBasedTest extends DeeSourceBasedTest {
	
	protected static final String TESTFILESDIR = "dtool.parser/parser-tests";
	
	private static final int PARSER_SOURCE_BASED_TESTS_COUNT = 85;
	private static int splitTestCount = 0;
	
	@Parameters
	public static Collection<Object[]> filesToParse() throws IOException {
		return getTestFilesFromFolderAsParameterList(DToolTestResources.getTestResource(TESTFILESDIR));
	}
	
	protected final File file;
	
	public DeeParserSourceBasedTest(File file) {
		this.file = file;
	}
	
	@Test
	public void runSourceBasedTests() throws IOException {
		for (AnnotatedSource testString : getSourceBasedTests(file)) {
			splitTestCount++;
			runSourceBasedTest(testString);
		}
	}
	
	@AfterClass
	public static void checkTestCount() {
		assertTrue(splitTestCount == PARSER_SOURCE_BASED_TESTS_COUNT);
	}
	
	public void runSourceBasedTest(AnnotatedSource testSource) {
		String parseSource = testSource.source;
		String expectedGenSource = parseSource;
		boolean allowAnyErrors = false;
		
		ArrayList<ParserError> expectedErrors = new ArrayList<ParserError>();
		
		for (MetadataEntry mde : testSource.metadata) {
			if(mde.name.equals("AST_EXPECTED")) {
				assertTrue(expectedGenSource == parseSource);
				expectedGenSource = mde.associatedSource;
			} else if(mde.name.equals("error")){
				expectedErrors.add(decodeError(parseSource, mde));
			} else if(mde.name.equals("parser") && mde.value.equals("AllowAnyErrors")){
				allowAnyErrors = true;
			} else if(mde.name.equals("parser") && mde.value.equals("DontCheckSourceEquality")){
				expectedGenSource = null;
			} else {
				assertFail("Unknown metadata");
			}
		}
		
		DeeParserTest.runParserTest(parseSource, expectedGenSource, expectedErrors, allowAnyErrors);
	}
	
	public ParserError decodeError(String parseSource, MetadataEntry mde) {
		String errorType = StringUtil.upUntil(mde.value, "_");
		String errorParam = NewUtils.fromIndexOf("_", mde.value);
		
		DeeLexer deeLexer = new DeeLexer(parseSource);
		
		SourceRange errorRange = mde.getSourceRange();
		
		 if(errorType.equals("ITC")) {
			return new ParserError(EDeeParserErrors.INVALID_TOKEN_CHARACTERS, errorRange, mde.associatedSource, null);
		} else if(errorType.equals("BT")) {
			// TODO errorParam
			return new ParserError(EDeeParserErrors.MALFORMED_TOKEN, errorRange, null, null);
		} else if(errorType.equals("EXP") || errorType.equals("EXPRULE")) {
			String errorSource = mde.associatedSource;
			
			if(mde.associatedSource == null) {
				Token lastToken = findLastEffectiveTokenBeforeOffset(mde.offset, deeLexer);
				errorRange = DeeParser.sr(lastToken);
				errorSource = lastToken.tokenSource;
			}
			if(errorType.equals("EXP")) {
				String expectedTokenStr = DeeLexerTest.transformTokenNameAliases(errorParam);
				return new ParserError(EDeeParserErrors.EXPECTED_TOKEN, errorRange, errorSource, expectedTokenStr);
			} else { // EXPRULE
				errorParam = getExpectedRuleName(errorParam);
				return new ParserError(EDeeParserErrors.EXPECTED_RULE, errorRange, errorSource, errorParam);
			}
		} else if(errorType.equals("SE")) {
			assertNotNull(mde.associatedSource);
			errorParam = getExpectedRuleName(errorParam);
			return new ParserError(EDeeParserErrors.SYNTAX_ERROR, errorRange, mde.associatedSource, errorParam);
		} else {
			throw assertFail();
		}
	}
	
	public String getExpectedRuleName(String errorParam) {
		if(errorParam.equals("decl")) {
			errorParam = DeeParser.DECLARATION_RULE;
		} else if(errorParam.equals("exp")) {
			errorParam = DeeParser.EXPRESSION_RULE;
		}
		return errorParam;
	}
	
	public Token findLastEffectiveTokenBeforeOffset(int offset, DeeLexer deeLexer) {
		assertTrue(offset <= deeLexer.source.length());
		
		Token lastNonIgnoredToken = null;
		while(true) {
			Token token = deeLexer.next();
			if(token.getStartPos() >= offset || token.getEndPos() > offset) {
				assertNotNull(lastNonIgnoredToken);
				deeLexer.reset(lastNonIgnoredToken.startPos);
				break;
			}
			if(token.type.isParserIgnored) {
				continue;
			}
			lastNonIgnoredToken = token;
		}
		return lastNonIgnoredToken;
	}
	
}