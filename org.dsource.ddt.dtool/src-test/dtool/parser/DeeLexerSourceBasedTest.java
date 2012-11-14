package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import dtool.tests.DToolBaseTest;
import dtool.tests.DToolTestResources;

@RunWith(Parameterized.class)
public class DeeLexerSourceBasedTest extends DToolBaseTest {
	
	protected static final String TESTFILESDIR = "dtool.parser/lexer-tests";
	
	@Parameters
	public static Collection<Object[]> filesToParse() throws IOException {
		return getTestFilesFromFolderAsParameterList(DToolTestResources.getTestResource(TESTFILESDIR));
	}
	
	protected static final String LEXERTEST_KEYWORD = "/+#LEXERTEST";
	protected String source;
	
	public DeeLexerSourceBasedTest(File file) {
		this(readStringFromFileUnchecked(file));
	}
	
	protected DeeLexerSourceBasedTest(String source) {
		this.source = source;
	}
	
	@Test
	public void runSourceBasedTest() throws IOException {
		runLexerSourceBasedTest(source);
	}
	
	
	public void runLexerSourceBasedTest(String testSource) {
		int lexerSourceEnd = testSource.indexOf(LEXERTEST_KEYWORD);
		
		int index = lexerSourceEnd + LEXERTEST_KEYWORD.length();
		String divisor;
		if(testSource.charAt(index) == '\n') {
			divisor = "\n";
		} else {
			divisor = "\r\n";
			assertEquals(testSource.substring(index, index+2), divisor);
		}
		index+=2;
		String expectedString = testSource.substring(index);
		String[] expectedLines = expectedString.split(divisor);
		
		DeeTokens[] expectedTokens = new DeeTokens[expectedLines.length-1];
		for (int i = 0; i < expectedLines.length; i++) {
			String expectedLine = expectedLines[i];
			if(expectedLine.equals("+/"))
				break;
			try {
				DeeTokens expectedToken = DeeTokens.valueOf(expectedLine);
				expectedTokens[i] = expectedToken;
			} catch(IllegalArgumentException e) {
				assertFail();
			}
		}
		
		String source = testSource.substring(0, lexerSourceEnd);
		DeeLexerTest.runLexerTest(source, expectedTokens);
	}
	
}