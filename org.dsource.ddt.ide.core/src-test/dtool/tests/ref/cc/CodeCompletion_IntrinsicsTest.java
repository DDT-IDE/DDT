package dtool.tests.ref.cc;


import org.junit.Test;

import dtool.ast.IASTNeoVisitor;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.EArcheType;
import dtool.refmodel.IScopeNode;

public class CodeCompletion_IntrinsicsTest extends CodeCompletion__Common {
	
	public static final String TEST_SRCFILE = "testCodeCompletion_intrinsics.d";
	
	public CodeCompletion_IntrinsicsTest() {
		super(TEST_SRCFILE);
	}
	
	
	/* ------------- Tests -------------  */
	
	@Test
	public void test1() throws Exception {
		checkProposals(
				list(mockDefUnit("Object"), mockDefUnit("ClassInfo"), mockDefUnit("TypeInfo"), mockDefUnit("Error")),
				array("Error", "ClassInfo"), true);
	}
	
	public DefUnit mockDefUnit(String name) throws Exception {
		return new DefUnit(name) {
			@Override
			public void accept0(IASTNeoVisitor visitor) {
			}
			
			@Override
			public IScopeNode getMembersScope() {
				return null;
			}
			
			@Override
			public EArcheType getArcheType() {
				return null;
			}
		};
	}
	
//	@Test
//	public void test2() throws Exception {
//		ccTester.testComputeProposals(getMarkerStartOffset("/+@CC1+/"), 0, true,
//				"Object",
//				"Classinfo"
//		);
//	}
	
}

