package dtool.resolver;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import melnorme.utilbox.core.Function;
import melnorme.utilbox.misc.StringUtil;
import dtool.ast.definitions.IDefElement;
import dtool.tests.DToolBaseTest;

/**
 * This is an old version of what {@link DefUnitResultsChecker} does now
 */
public class CompareDefUnits extends DToolBaseTest {
	
	public static Function<String, String> fnStringToSubString(final int index) {
		return new Function<String, String>() {
			@Override
			public String evaluate(String obj) {
				return (obj == null || index > obj.length() )? null : obj.substring(index);
			}
		};
	}
	
	public static Function<IDefElement, String> fnDefUnitToStringAsElement(final int prefixLen) {
		return new Function<IDefElement, String>() {
			@Override
			public String evaluate(IDefElement defUnit) {
				return defUnit == null ? null : defUnit.getExtendedName().substring(prefixLen);
			}
		};
	}
	
	public static Function<IDefElement, String> fnDefUnitToStringAsName() {
		return new Function<IDefElement, String>() {
			@Override
			public String evaluate(IDefElement obj) {
				return obj == null ? null : obj.getName();
			}
		};
	}
	
	public static void checkResults(Collection<? extends IDefElement> originaResults, String[] expectedProposalsArr) {
		Collection<IDefElement> results = new ArrayList<>(originaResults);
		DefUnitResultsChecker.removeIgnoredDefUnits(results, false, true);
		
		HashSet<String> expectedProposals = hashSet(expectedProposalsArr);
		HashSet<String> resultProposals = hashSet(strmap(results, fnDefUnitToStringAsElement(0)));
		
		assertTrue(resultProposals.contains(null) == false);
		
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