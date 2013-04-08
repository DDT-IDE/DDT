package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import dtool.tests.DToolTestResources;

// TODO retire this

@RunWith(Parameterized.class)
@Deprecated
public class ParserTestDataFilesTest extends Parser__FileParseTest {
	
	private static final String SPLIT_MARKER = "/+__ ";
	private static final String SPLIT_MARKER_END = "__+/";
	private static final String INVALID_SYNTAX = " INVALID ";
	/** Stuff that should be parsed as invalid but is not due to parser limitation*/
	private static final String UNSUPPORTED_INVALID = " UNSUPPORTED_INVALID ";
	
	
	@Parameters(name="{index}: {0}")
	public static Collection<Object[]> filesToParse() throws IOException {
		return getTestFilesFromFolderAsParameterList(DToolTestResources.getTestResource(TESTFILESDIR), true);
	}
	
	public ParserTestDataFilesTest(String testDescription, File file) {
		super(testDescription, file);
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
			if(instructions.indexOf(INVALID_SYNTAX) != -1) {
				expectErrors = true;
			} else if(instructions.indexOf(UNSUPPORTED_INVALID) != -1) {
				expectErrors = null;
			} else {
				assertFail();
			}
			
			testParseTestFile(source.substring(splitMarkerEnd + SPLIT_MARKER_END.length()), expectErrors);
		} else {
			testParse(source, expectErrors, true);
		}
	}
	
}