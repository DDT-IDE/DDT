package dtool.parser;

import org.junit.Test;

import dtool.tests.CommonDToolTest;

public abstract class CommonParameterizedTest extends CommonDToolTest {
	
	protected final Runnable testRunnable;
	
	public CommonParameterizedTest(@SuppressWarnings("unused") String testUIDescription, Runnable testRunnable) {
		this.testRunnable = testRunnable;
	}
	
	@Test
	public void testname() throws Exception {
		testRunnable.run();
	}
	
}
