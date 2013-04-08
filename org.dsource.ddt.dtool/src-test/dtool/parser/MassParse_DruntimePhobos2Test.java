package dtool.parser;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class MassParse_DruntimePhobos2Test extends MassParse__CommonTest {
	
	@Parameters(name="{index}: {0}")
	public static Collection<Object[]> filesToParse() throws IOException {
		return getTestFilesFromFolderAsParameterList(getCommonResource(TESTSRC_DRUNTIME_PHOBOS2), true);
	}
	
	public MassParse_DruntimePhobos2Test(String testDescription, File file) {
		super(testDescription, file);
	}
	
	@Override
	protected boolean failOnSyntaxErrors() {
		// TODO: Should be true here, but the parser is not up to date with D2 yet
		return false;
	}
	
}
