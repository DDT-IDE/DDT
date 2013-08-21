package dtool.resolver;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import melnorme.utilbox.core.Function;
import melnorme.utilbox.misc.StringUtil;
import dtool.ast.definitions.DefUnit;
import dtool.tests.DToolBaseTest;

/**
 * This is an old version of what {@link DefUnitResultsChecker} does now
 */
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
	
	public static Function<String, String> fnStringToSubString(final int index) {
		return new Function<String, String>() {
			@Override
			public String evaluate(String obj) {
				return (obj == null || index > obj.length() )? null : obj.substring(index);
			}
		};
	}
	
	public static Function<DefUnit, String> fnDefUnitToStringAsElement(final int prefixLen) {
		return new Function<DefUnit, String>() {
			@Override
			public String evaluate(DefUnit defUnit) {
				return defUnit == null ? null : defUnit.getExtendedName().substring(prefixLen);
			}
		};
	}
	
	public static Function<DefUnit, String> fnDefUnitToStringAsName() {
		return new Function<DefUnit, String>() {
			@Override
			public String evaluate(DefUnit obj) {
				return obj == null ? null : obj.getName();
			}
		};
	}
	
	public static void checkResults(Collection<DefUnit> originaResults, String[] expectedProposalsArr, 
		boolean removeIntrinsics) {
		Collection<DefUnit> results = new ArrayList<>(originaResults);
		DefUnitResultsChecker.removeIgnoredDefUnits(results, false, true);
		
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
	
	public static void assertEqualSet(Set<?> result, Set<?> expected) {
		boolean equals = result.equals(expected);
		if(equals) {
			return;
		}
		HashSet<?> resultExtra = removeAllCopy(result, expected);
		HashSet<?> expectedMissing = removeAllCopy(expected, result);
		if(!resultExtra.isEmpty()) {
			System.out.println("--- Unexpected elements ("+resultExtra.size()+") : ---\n" +
				StringUtil.collToString(resultExtra, "\n") );
		}
		if(!expectedMissing.isEmpty()) {
			System.out.println("--- Missing elements ("+expectedMissing.size()+") : ---\n" +
				StringUtil.collToString(expectedMissing, "\n") );
		}
		assertFail();
	}
	
}