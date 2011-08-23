package dtool.tests.ref.cc;


import org.junit.Test;

public class CodeCompletion_n2Test extends CodeCompletion__Common {
	
	public static final String TEST_SRCFILE = "testCodeCompletion2.d";
	
	public CodeCompletion_n2Test() {
		super(TEST_SRCFILE);
	}
	
	
	/* ------------- Tests -------------  */
	
	@Test
	public void test1() throws Exception {
		testComputeProposals(getMarkerStartOffset("/+@CC1+/"), 1, false,
				"func()", "foobarvar",
				"foovar", "foox",  "foo_t", "fooOfModule"
		);
	}
	
	@Test
	public void test2() throws Exception {
		int offset = getMarkerStartOffset("/+@CC2+/");
		testComputeProposals(offset, 1, false,
				"foovar", "foox" 
		);
		
		testComputeProposalsWithRepLen(offset-1, 0, 1, false,
				"foovar", "foox", "baz" 
		);
	}
	
	@Test
	public void test3() throws Exception {
		int offset = getMarkerStartOffset("/+@CC3+/");
		testComputeProposals(offset, 1, false,
				"fooOfModule", "foo_t"
		);
		
		testComputeProposalsWithRepLen(offset-1, 0, 1, false,
				"Foo", "fooOfModule", "foo_t", "ix", "FooBar",
				"pack", "nonexistantmodule"
				,"othervar", "Other" 
		);
	}
	
	@Test
	public void test4() throws Exception {
		testComputeProposals(getMarkerStartOffset("/+@CC4+/"), 0, false,
				"foovar", "foox", "baz" 
		);
	}
	
}

