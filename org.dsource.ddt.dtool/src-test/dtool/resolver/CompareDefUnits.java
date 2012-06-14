package dtool.resolver;

import static dtool.tests.MiscDeeTestUtils.fnDefUnitToStringAsElement;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import melnorme.utilbox.core.Function;
import dtool.ast.definitions.DefUnit;
import dtool.tests.DToolBaseTest;

public class CompareDefUnits extends DToolBaseTest {
	
	public static String[] INTRINSIC_DEFUNITS = new String[] {
		"bit", "size_t", "ptrdiff_t", "hash_t", "string", "wstring", "dstring",
		"printf(char*, ...)", "trace_term()", "Object", "Interface", "ClassInfo",
		"OffsetTypeInfo", "TypeInfo",
		"TypeInfo_Typedef", "TypeInfo_Enum", "TypeInfo_Pointer", "TypeInfo_Array",
		"TypeInfo_StaticArray", "TypeInfo_AssociativeArray", "TypeInfo_Function", "TypeInfo_Delegate",
		"TypeInfo_Class", "TypeInfo_Interface", "TypeInfo_Struct", "TypeInfo_Tuple", "TypeInfo_Const",
		"TypeInfo_Invariant",
		"MemberInfo", "MemberInfo_field", "MemberInfo_function", "Exception", "Error"
	};
	
	public static Set<String> INTRINSIC_DEFUNITS_SET = unmodifiable(hashSet(INTRINSIC_DEFUNITS));
	
	
	public static Function<DefUnit, String> defUnitStringAsElementMapper = new Function<DefUnit, String>() {
		@Override
		public String evaluate(DefUnit obj) {
			return obj == null ? null : obj.toStringAsElement();
		}
	};
	
	
	public static void checkResults(Collection<DefUnit> results, String[] expectedProposalsArr, boolean removeIntrinsics) {
		HashSet<String> expectedProposals = hashSet(expectedProposalsArr);
		HashSet<String> resultProposals = hashSet(strmap(results, fnDefUnitToStringAsElement(0)));
		
		if(removeIntrinsics) {
			// Don't remove intrinsics which are explicitly expected
			HashSet<String> intrinsicsProposals = hashSet(INTRINSIC_DEFUNITS);
			Set<String> intrinsicsProposalsToRemove = removeAllCopy(intrinsicsProposals, expectedProposals);
			resultProposals.removeAll(intrinsicsProposalsToRemove);
		}
		resultProposals.remove(null);
		
		assertEqualSet(resultProposals, expectedProposals);
	}
	
}
