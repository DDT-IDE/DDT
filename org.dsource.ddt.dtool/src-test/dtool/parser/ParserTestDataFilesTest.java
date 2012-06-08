package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

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
	
	private static final String SPLIT_MARKER = "/+__ ";
	private static final String SPLIT_MARKER_END = "__+/";
	private static final String INVALID_SYNTAX = "INVALID";
	
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
		
		testParseTestFile(source, false);
	}
	
	public void testParseTestFile(String source, Boolean expectErrors) {
		
		int splitMarker = source.indexOf(SPLIT_MARKER);
		if(splitMarker != -1) {
			testParse(source.substring(0, splitMarker), expectErrors, true);
			
			int splitMarkerEnd = source.indexOf(SPLIT_MARKER_END, splitMarker);
			assertTrue(splitMarkerEnd != -1);
			String instructions = source.substring(splitMarker, splitMarkerEnd);
			int keywordMarker = instructions.indexOf(INVALID_SYNTAX);
			assertTrue(keywordMarker != -1);
			testParseTestFile(source.substring(splitMarkerEnd + SPLIT_MARKER_END.length()), true);
		} else {
			testParse(source, expectErrors, true);
		}
	}
	
}