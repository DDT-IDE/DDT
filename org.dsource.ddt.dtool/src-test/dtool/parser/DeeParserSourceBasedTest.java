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
import dtool.tests.AnnotatedSource;
import dtool.tests.AnnotatedSource.MetadataEntry;
import dtool.tests.DToolTestResources;

@RunWith(Parameterized.class)
public class DeeParserSourceBasedTest extends DeeSourceBasedTest {
	
	protected static final String TESTFILESDIR = "dtool.parser/parser-tests";
	
	private static final int PARSER_SOURCE_BASED_TESTS_COUNT = 88;
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
			} else if(mde.name.equals("parser") && mde.extraValue.equals("AllowAnyErrors")){
				allowAnyErrors = true;
			} else if(mde.name.equals("parser") && mde.extraValue.equals("DontCheckSourceEquality")){
				expectedGenSource = null;
			} else {
				assertFail("Unknown metadata");
			}
		}
		
		DeeParserTest.runParserTest(parseSource, expectedGenSource, expectedErrors, allowAnyErrors);
	}
	
	public ParserError decodeError(String parseSource, MetadataEntry mde) {
		String errorType = StringUtil.upUntil(mde.extraValue, "_");
		String errorParam = StringUtil.fromLastIndexOf("_", mde.extraValue);
		
		DeeLexer deeLexer = new DeeLexer(parseSource);
		
		SourceRange errorRange = mde.sourceRange;
		
		if(errorType.equals("EXP")) {
			String expectedTokenStr = DeeLexerTest.transformTokenNameAliases(errorParam);
			String errorSource = mde.associatedSource;
			
			if(mde.associatedSource == null) {
				Token lastToken = findLastEffectiveTokenBeforeOffset(mde.sourceRange.getOffset(), deeLexer);
				errorRange = DeeParser.sr(lastToken);
				errorSource = lastToken.value;
			}
			return new ParserError(EDeeParserErrors.EXPECTED_TOKEN, errorRange, errorSource, expectedTokenStr);
		} else if(errorType.equals("UT")) {
			return new ParserError(EDeeParserErrors.UNKNOWN_TOKEN, errorRange, mde.associatedSource, null);
		} else if(errorType.equals("IT")) {
			// TODO errorParam
			return new ParserError(EDeeParserErrors.MALFORMED_TOKEN, errorRange, null, null);
		} else {
			throw assertFail();
		}
	}
	
	public Token findLastEffectiveTokenBeforeOffset(int offset, DeeLexer deeLexer) {
		assertTrue(offset <= deeLexer.source.length());
		
		Token lastNonIgnoredToken = null;
		while(true) {
			Token token = deeLexer.next();
			if(token.getStartPos() >= offset || token.getEndPos() > offset) {
				assertNotNull(lastNonIgnoredToken);
				deeLexer.reset(lastNonIgnoredToken.start);
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