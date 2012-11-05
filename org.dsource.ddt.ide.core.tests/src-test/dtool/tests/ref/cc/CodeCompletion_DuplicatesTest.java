package dtool.tests.ref.cc;


import org.junit.Test;

public class CodeCompletion_DuplicatesTest extends CodeCompletion__Common {
	
	public static final String TEST_SRCFILE = "testCodeCompletion_dups.d";
	
	public CodeCompletion_DuplicatesTest() {
		super(TEST_SRCFILE);
	}
	
	
	/* ------------- Tests -------------  */
	
	
	@Test
	public void test1() throws Exception {
		int offset = getMarkerStartOffset("/+@CC1+/");
		testComputeProposals(offset, 1, false,
				
				"func(int aaa, List!(Foo) aaa)",
				"func(int bbb, List!(Foo) bbb)",
				"func(char a, List!(Foo) a)",
				"func(int a, List!(Bar) a)",
				"func()",
				
				
				"foo_t", "fooalias" 
		);
	}
	
	@Test
	public void test2() throws Exception {
		int offset = getMarkerStartOffset("/+@CC2+/");
		testComputeProposals(offset, 1, false,
				"foolocalinner", "foolocal1", "fParam", "foobarvar",
				
				"func(int a, List!(Foo) a)",
				"func(int bbb, List!(Foo) bbb)",
				"func(char a, List!(Foo) a)",
				"func(int a, List!(Bar) a)",
				"func()",
				
				"func(int aaa, List!(Foo) aaa)", //TODO: should be removed due to overload sets
				
				"foovar", "foox", 
				
				"foo_t", "fooalias" 
		);
	}
	
}
