package dtool.parser;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;


public abstract class Parser__FileParseTest extends Parser__CommonTest {
	
	public static final String COMMON = "common/";
	
	protected final File file;
	
	public Parser__FileParseTest(File file) {
		this.file = file;
		assertTrue(file.isFile());
	}
	
	@Test
	public void testParseFile() throws IOException {
		parseFile(file, failOnSyntaxErrors(), checkSourceRanges());
	}
	
	protected boolean failOnSyntaxErrors() {
		return true;
	}
	
	protected boolean checkSourceRanges() {
		return false;
	}
	
	protected static void parseFile(File file, boolean failOnSyntaxErrors, boolean checkSourceRanges) {
		String source = readStringFromFileUnchecked(file);
		testParse(source, failOnSyntaxErrors ? false : null, checkSourceRanges);
	}
	
}