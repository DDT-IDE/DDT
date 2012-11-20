package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.regex.Matcher;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import dtool.tests.DToolBaseTest;
import dtool.tests.DToolTestResources;
import dtool.tests.MiscDeeTestUtils;

@RunWith(Parameterized.class)
public class DeeLexerSourceBasedTest extends DToolBaseTest {
	
	protected static final String TESTFILESDIR = "dtool.parser/lexer-tests";
	
	@Parameters
	public static Collection<Object[]> filesToParse() throws IOException {
		return getTestFilesFromFolderAsParameterList(DToolTestResources.getTestResource(TESTFILESDIR));
	}
	
	protected static final String LEXERTEST_KEYWORD = "/+#LEXERTEST";
	protected final File file;
	
	public DeeLexerSourceBasedTest(File file) {
		this.file = file;
	}
	
	@Test
	public void runSourceBasedTest() throws IOException {
		String[] splitSourceBasedTests = enteringSourceBasedTest(file);
		for (String testString : splitSourceBasedTests) {
			runLexerSourceBasedTest(testString);
		}
	}
	
	public static final String ANY_UNTIL_NEWLINE_REGEX = "[^\\\r\\\n]*\\\r?\\\n";
	
	
	public void runLexerSourceBasedTest(String testSource) {
		int lexerSourceEnd = testSource.indexOf(LEXERTEST_KEYWORD);
		assertTrue(lexerSourceEnd != -1);
		int index = lexerSourceEnd + LEXERTEST_KEYWORD.length();
		
		Matcher matcher = MiscDeeTestUtils.matchRegexp(ANY_UNTIL_NEWLINE_REGEX, testSource, index);
		
		String expectedTokensData = testSource.substring(matcher.end());
		String[] expectedLines = expectedTokensData.split("(\\\r?\\\n)|,");
		
		DeeTokens[] expectedTokens = new DeeTokens[expectedLines.length-1];
		for (int i = 0; i < expectedLines.length; i++) {
			String expectedLine = expectedLines[i].trim();
			if(expectedLine.endsWith("+/"))
				break;
			try {
				if(expectedLine.equals("*")) {
					expectedTokens[i] = null;
				} else {
					DeeTokens expectedToken = DeeTokens.valueOf(expectedLine);
					expectedTokens[i] = expectedToken;
				}
			} catch(IllegalArgumentException e) {
				assertFail();
			}
		}
		
		String source = testSource.substring(0, lexerSourceEnd);
		DeeLexerTest.runLexerTest(source, expectedTokens);
	}
	
}