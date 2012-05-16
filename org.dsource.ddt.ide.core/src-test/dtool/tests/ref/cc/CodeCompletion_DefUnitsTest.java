package dtool.tests.ref.cc;


import mmrnmhrm.tests.ITestResourcesConstants;
import mmrnmhrm.tests.SampleMainProject;

import org.junit.Test;

public class CodeCompletion_DefUnitsTest extends CodeCompletion__Common {
	
	public static final String TEST_SRCFILE = "sampledefs.d";
	
	public CodeCompletion_DefUnitsTest() {
		super(SampleMainProject.getSourceModule(ITestResourcesConstants.TR_SAMPLE_SRC1, TEST_SRCFILE));
	}
	
	
	/* ------------- Tests -------------  */

	@Test
	public void test1() throws Exception {
		testComputeProposals(getMarkerStartOffset("/+@CC1+/"), 0, true,
				"IfTypeDefUnit",
				"parameter",
				"tplFunc(asdf.qwer parameter)",
				"TplNestedClass",
				
				"sampledefs",
				"functionFoo(int fooParam)",
				"Alias",
				"Class",
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
				"EnumMemberA", "EnumMemberB"
		);
	}
	
	@Test
	public void testCtor() throws Exception {
		testComputeProposals(getMarkerStartOffset("/+CC-ctor@+/"), 4, true,
				"ctorParam"
		);
	}
}
