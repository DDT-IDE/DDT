package dtool.tests.ref.cc;


import org.junit.Test;

public class CodeCompletion_DefUnitsTest extends CodeCompletion__Common {
	
	public static final String TEST_SRCFILE = "sampledefs.d";
	
	public CodeCompletion_DefUnitsTest() {
		super(TEST_SRCFILE);
	}
	
	
	/* ------------- Tests -------------  */

	@Test
	public void test1() throws Exception {
		testComputeProposals(getMarkerStartOffset("/+@CC1+/"), 0, true,
				"IfTypeDefUnit",
				"parameter",
				"func(asdf.qwer parameter)",
				"Class",
				
				"sampledefs",
				"Alias",
				"Enum",
				"Interface",
				"Struct",
				"Typedef",
				"Union",
				"variable",
				"ImportSelectiveAlias",
				"ImportAliasingDefUnit",
				"pack",
				
				"Template",
				"TypeParam",
				"ValueParam",
				"AliasParam",
				"TupleParam"
		);
		
	}
	
	@Test
	public void test2() throws Exception {
		testComputeProposals(getMarkerStartOffset("/+@CC2+/"), 1, false,
				"numMemberA", "numMemberB"
		);
	}
}
