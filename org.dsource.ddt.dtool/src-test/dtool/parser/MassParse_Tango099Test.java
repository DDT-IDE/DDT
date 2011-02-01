package dtool.parser;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class MassParse_Tango099Test extends MassParse__CommonTest {
	
	
	@Parameters
	public static Collection<Object[]> filesToParse() throws IOException {
		return getParseFileParameterList(getCommonResource(TESTSRC_TANGO_0_99));
	}
	
	public MassParse_Tango099Test(File file) {
		super(file);
	}
	
	@Override
	protected boolean failOnSyntaxErrors() {
		// allow syntax errors, because this is a D1 source.
		return false;
	}
	
}
