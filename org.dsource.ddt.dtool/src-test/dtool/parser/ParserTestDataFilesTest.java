package dtool.parser;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import dtool.tests.DToolTestResources;

@RunWith(Parameterized.class)
public class ParserTestDataFilesTest extends Parser__FileParseTest {
	
	private static final String INVALID_SYNTAX_MARKER = "//INVALID";

	@Parameters
	public static Collection<Object[]> filesToParse() throws IOException {
		return getTestFilesFromFolderAsParameterList(DToolTestResources.getTestResource(TESTFILESDIR));
	}
	
	public ParserTestDataFilesTest(File file) {
		super(file);
	}
	
	@Test
	@Override
	public void testParseFile() throws IOException {
		String source = readStringFromFileUnchecked(file);
		Boolean expectedErrors;
		if(source.contains(INVALID_SYNTAX_MARKER)) {
			expectedErrors = true;
		} else {
			expectedErrors = false;
		}
		testParse(source, expectedErrors, true);
	}
	
}