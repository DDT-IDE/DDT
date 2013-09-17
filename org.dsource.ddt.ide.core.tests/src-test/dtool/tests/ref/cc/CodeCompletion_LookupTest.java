package dtool.tests.ref.cc;


import org.junit.Test;

public class CodeCompletion_LookupTest extends CodeCompletion__Common {
	
	public static final String TEST_SRCFILE = "testCodeCompletion.d";
	
	public CodeCompletion_LookupTest() {
		super(TEST_SRCFILE);
	}
	
	
	/* ------------- Tests -------------  */
	
	//@Test
	public void test0() throws Exception {
		//testComputeProposals(0, 0);
	}
	
	@Test
	public void test1() throws Exception {
		testComputeProposals(getMarkerEndOffset("/+CC1@+/"), 0, 
				"fParam",
				"foobarvar", "ix", "func(int a, List!(Foo) a)", "test(int fParam)",  
				"foovar", "foox", "baz",
				"Foo", "fooOfModule", "frak", "fooalias", "foo_t", 
				/*"ix",*/ "FooBar", "Xpto", "func(char b, List!(Foo) b)", "func()",
				"pack", "nonexistantmodule",
				"othervar", "Other",
				"testCodeCompletion"
		);
	}
	
	public static final String[] EXPECTED_IN_TEST_f = array(
			"fParam", "func(int a, List!(Foo) a)", "foobarvar",
			"foovar", "foox", 
			"func(char b, List!(Foo) b)", "func()",
			/*"FooBar",*/  "foo_t", "fooalias", "fooOfModule", "frak" /*,"Foo",*/
	);
	
	public static final String[] EXPECTED_IN_TEST_fo = array(
			"foobarvar",
			"foovar", "foox", "foo_t", "fooalias", "fooOfModule"
	);
	
	@Test
	public void test2() throws Exception {
		testComputeProposals(getMarkerEndOffset("/+CC2@+/")+1, 1, EXPECTED_IN_TEST_f);
		
		// same test, but having characters ahead of offset
		testComputeProposalsWithRepLen(getMarkerEndOffset("/+CC3@+/")+1, 1, 2, EXPECTED_IN_TEST_f);
	}
	
	@Test
	public void test3() throws Exception {
		testComputeProposals(getMarkerEndOffset("/+CC3@+/")+3, 3, 
				"foobarvar",
				"foovar", "foox", "foo_t", "fooalias", "fooOfModule"
		);
	}
	
	@Test
	public void test4() throws Exception {
		testComputeProposals(getMarkerEndOffset("/+CC4@+/")+2, 2, 
				EXPECTED_IN_TEST_fo
		);
	}
	
}