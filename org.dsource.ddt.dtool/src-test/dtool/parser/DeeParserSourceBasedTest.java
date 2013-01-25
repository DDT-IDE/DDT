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

import static dtool.parser.DeeParserTest.runParserTest______________________;
import static dtool.tests.DToolTestResources.getTestResource;
import static dtool.util.NewUtils.assertNotNull_;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.CoreUtil.areEqual;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import melnorme.utilbox.core.Predicate;
import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.misc.StringUtil;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import dtool.ast.SourceRange;
import dtool.parser.ParserError.EDeeParserErrors;
import dtool.sourcegen.AnnotatedSource;
import dtool.sourcegen.AnnotatedSource.MetadataEntry;
import dtool.tests.SimpleParser;
import dtool.util.NewUtils;

@RunWith(Parameterized.class)
public class DeeParserSourceBasedTest extends DeeSourceBasedTest {
	
	protected static final String TESTFILESDIR = "dtool.parser/parser-tests";
	
	@Parameters
	public static Collection<Object[]> filesToParse() throws IOException {
		Predicate<File> nameFilter = new Predicate<File>() {
			@Override
			public boolean evaluate(File file) {
				if(file.getName().endsWith("_TODO"))
					return true;
				return false;
			}
		};
		return toFnParameterList(getDeeModuleList(getTestResource(TESTFILESDIR), true), nameFilter);
	}
	
	protected final File file;
	
	public DeeParserSourceBasedTest(File file) {
		this.file = file;
	}
	
	
	@Test
	public void runSourceBasedTests() throws Exception { runSourceBasedTests$(); }
	public void runSourceBasedTests$() throws Exception {
		AnnotatedSource[] sourceBasedTests = getSourceBasedTests(file);
		for (AnnotatedSource testString : sourceBasedTests) {
			testsLogger.println(">> ----------- Annotated Source test: ----------- <<");
			testsLogger.println(testString.source);
			runSourceBasedTest(testString);
		}
	}
	
	public void runSourceBasedTest(AnnotatedSource testSource) {
		String parseSource = testSource.source;
		String expectedGenSource = parseSource;
		NamedNodeElement[] expectedStructure = null;
		boolean allowAnyErrors = false;
		boolean ignoreFurtherErrorMDs = false;
		
		ArrayList<ParserError> expectedErrors = new ArrayList<ParserError>();
		
		for (MetadataEntry mde : testSource.metadata) {
			if(mde.name.equals("AST_EXPECTED")) {
				assertTrue(expectedGenSource == parseSource);
				expectedGenSource = mde.associatedSource;
				ignoreFurtherErrorMDs = true;
			} else if(mde.name.equals("AST_STRUCTURE_EXPECTED")) {
				assertTrue(expectedStructure == null);
				expectedStructure = processExpectedStructure(mde.associatedSource);
			} else if(mde.name.equals("error")){
				if(!ignoreFurtherErrorMDs) {
					expectedErrors.add(decodeError(parseSource, mde));
				}
			} else if(mde.name.equals("parser") && mde.value.equals("AllowAnyErrors")){
				allowAnyErrors = true;
			} else if(mde.name.equals("parser") && mde.value.equals("DontCheckSourceEquality")){
				expectedGenSource = null;
			} else {
				// TODO remove TODO flag
				if(!(areEqual(mde.value, "flag") || areEqual(mde.name, "TODO")))
					assertFail("Unknown metadata");
			}
		}
		
		runParserTest______________________(
			parseSource, expectedGenSource, expectedStructure, expectedErrors, allowAnyErrors);
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
		} else if(errorType.equals("EXP")) {
			String expectedTokenStr = DeeLexerTest.transformTokenNameAliases(errorParam);
			return createErrorToken(EDeeParserErrors.EXPECTED_TOKEN, mde, deeLexer, true, expectedTokenStr);
		} else if(errorType.equals("EXPRULE")) {
			errorParam = getExpectedRuleName(errorParam);
			return createErrorToken(EDeeParserErrors.EXPECTED_RULE, mde, deeLexer, true, errorParam);
		} else if(errorType.equals("SE")) {
			errorParam = getExpectedRuleName(errorParam);
			return createErrorToken(EDeeParserErrors.SYNTAX_ERROR, mde, deeLexer, false, errorParam);
		} else if(mde.value.equals("BAD_LINKAGE_ID")) {
			return createErrorToken(EDeeParserErrors.INVALID_EXTERN_ID, mde, deeLexer, true, null);
		} else {
			throw assertFail();
		}
	}
	
	public ParserError createErrorToken(EDeeParserErrors errorTypeTk, MetadataEntry mde, DeeLexer deeLexer,
		boolean tokenBefore, String errorParam) {
		String errorSource = mde.associatedSource;
		SourceRange errorRange = mde.getSourceRange();
		
		if(mde.associatedSource == null) {
			Token lastToken = tokenBefore 
				? findLastEffectiveTokenBeforeOffset(mde.offset, deeLexer)
				: findNextEffectiveTokenAfterOffset(mde.offset, deeLexer);
			errorRange = DeeParser.sr(lastToken);
			errorSource = lastToken.tokenSource;
		}
		return new ParserError(errorTypeTk, errorRange, errorSource, errorParam);
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
	
	public Token findNextEffectiveTokenAfterOffset(int offset, DeeLexer deeLexer) {
		assertTrue(offset <= deeLexer.source.length());
		
		while(true) {
			Token token = deeLexer.next();
			if(token.type == DeeTokens.EOF) {
				assertFail();
			}
			if(token.type.isParserIgnored) {
				continue;
			}
			if(token.getStartPos() >= offset) {
				return token;
			}
			assertTrue(token.getEndPos() <= offset);
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
			return name + (children != null ? ( "("+StringUtil.collToString(children, " ")+")" ) : "");
		}
	}
	
	protected NamedNodeElement[] processExpectedStructure(String source) {
		SimpleParser parser = new SimpleParser(source);
		NamedNodeElement[] namedElements = readNamedElementsList(parser);
		assertTrue(parser.lookaheadIsEOF());
		return namedElements;
	}
	
	public static NamedNodeElement[] readNamedElementsList(SimpleParser parser) {
		ArrayList<NamedNodeElement> elements = new ArrayList<NamedNodeElement>();
		
		while(true) {
			String id;
			NamedNodeElement[] children = null;
			
			parser.seekWhiteSpace();
			if(parser.tryConsume("*")) {
				id = NamedNodeElement.IGNORE_ALL;
			} else {
				if(parser.tryConsume("?")) {
					id = NamedNodeElement.IGNORE_NAME;
				} else {
					id = parser.consumeAlphaNumericUS(true);
					if(id.isEmpty()) {
						break;
					}
				}
				if(parser.tryConsume("(")) {
					children = readNamedElementsList(parser);
					parser.seekWhiteSpace().consume(")");
				} else {
					children = new NamedNodeElement[0];
				}
			}
			elements.add(new NamedNodeElement(id, children));
		}
		assertTrue(parser.lookaheadIsEOF() || parser.lookAhead() == ')');
		
		return ArrayUtil.createFrom(elements, NamedNodeElement.class);
	}
	
}