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
				
				"unc(int aaa, List!(Foo) aaa)",
				"unc(int bbb, List!(Foo) bbb)",
				"unc(char a, List!(Foo) a)",
				"unc(int a, List!(Bar) a)",
				"unc()",
				
				
				"oo_t", "ooalias" 
		);
	}
	
	@Test
	public void test2() throws Exception {
		int offset = getMarkerStartOffset("/+@CC2+/");
		testComputeProposals(offset, 1, false,
				"oolocalinner", "oolocal1", "Param", "oobarvar",
				
				"unc(int a, List!(Foo) a)",
				"unc(int bbb, List!(Foo) bbb)",
				"unc(char a, List!(Foo) a)",
				"unc(int a, List!(Bar) a)",
				"unc()",
				
				"unc(int aaa, List!(Foo) aaa)", //TODO: should be removed due to overload sets
				
				"oovar", "oox", 
				
				"oo_t", "ooalias" 
		);
	}
	
}
