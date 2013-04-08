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
public class MassParse_MiscUnsupportedSyntaxCasesTest extends Parser__FileParseTest {
	
	@Parameters(name="{index}: {0}")
	public static Collection<Object[]> filesToParse() throws IOException {
		File scanDir = DToolTestResources.getTestResource(MassParse__CommonTest.COMMON + "miscCasesUnsupportedSyntax");
		return getTestFilesFromFolderAsParameterList(scanDir, true);
	}
	
	public MassParse_MiscUnsupportedSyntaxCasesTest(String testDescription, File file) {
		super(testDescription, file);
	}
	
	@Test
	@Override
	public void testParseFile() throws IOException {
		parseFile(file, false, true);
	}
	
}
