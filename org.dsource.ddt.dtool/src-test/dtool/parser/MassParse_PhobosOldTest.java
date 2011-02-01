package dtool.parser;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class MassParse_PhobosOldTest extends MassParse__CommonTest {
	
	@Parameters
	public static Collection<Object[]> filesToParse() throws IOException {
		return getParseFileParameterList(getCommonResource(TESTSRC_PHOBOS1_OLD));
	}
	
	public MassParse_PhobosOldTest(File file) {
		super(file);
	}
	
	@Override
	protected boolean failOnSyntaxErrors() {
		// allow syntax errors, because this is a D1 source.
		return false;
	}
	
}
