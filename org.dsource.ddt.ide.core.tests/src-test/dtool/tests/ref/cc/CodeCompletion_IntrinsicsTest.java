package dtool.tests.ref.cc;


import org.junit.Test;

import dtool.ast.IASTVisitor;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.EArcheType;
import dtool.refmodel.IScopeNode;
import dtool.refmodel.pluginadapters.IModuleResolver;
import dtool.resolver.CompareDefUnits;

public class CodeCompletion_IntrinsicsTest extends CodeCompletion__Common {
	
	public static final String TEST_SRCFILE = "testCodeCompletion_intrinsics.d";
	
	public CodeCompletion_IntrinsicsTest() {
		super(TEST_SRCFILE);
	}
	
	
	/* ------------- Tests -------------  */
	
	@Test
	public void test1() throws Exception {
		CompareDefUnits.checkResults(
				list(mockDefUnit("Object"), mockDefUnit("ClassInfo"), mockDefUnit("TypeInfo"), mockDefUnit("Error")),
				array("Error", "ClassInfo"), true);
	}
	
	public DefUnit mockDefUnit(String name) throws Exception {
		return new DefUnit(name) {
			@Override
			public void accept0(IASTVisitor visitor) {
			}
			
			@Override
			public IScopeNode getMembersScope(IModuleResolver moduleResolver) {
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

