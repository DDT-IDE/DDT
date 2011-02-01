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
public class MassParse_MiscCasesTest extends MassParse__CommonTest {
	
	@Parameters
	public static Collection<Object[]> filesToParse() throws IOException {
		File scanDir = new File(DToolTestResources.getInstance().getResourcesDir(), COMMON + "miscCases");
		return getParseFileParameterList(scanDir);
	}
	
	public MassParse_MiscCasesTest(File file) {
		super(file);
	}
	
	@Test
	@Override
	public void testParseFile() throws IOException {
		parseFile(file, true, true);
	}
	
}
