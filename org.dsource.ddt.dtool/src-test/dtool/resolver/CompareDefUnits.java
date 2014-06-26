package dtool.resolver;

import static dtool.resolver.DefUnitResultsChecker.strmap;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import melnorme.utilbox.core.fntypes.Function;
import melnorme.utilbox.misc.StringUtil;
import dtool.ast.definitions.INamedElement;
import dtool.tests.CommonDToolTest;

/**
 * This is an old version of what {@link DefUnitResultsChecker} does now
 */
@Deprecated
public class CompareDefUnits extends CommonDToolTest {
	
	public static Function<INamedElement, String> fnDefUnitToStringAsElement(final int prefixLen) {
		return new Function<INamedElement, String>() {
			@Override
			public String evaluate(INamedElement defUnit) {
				return defUnit == null ? null : defUnit.getExtendedName().substring(prefixLen);
			}
		};
	}
	
	public static Function<INamedElement, String> fnElementToFullyQualifiedName() {
		return new Function<INamedElement, String>() {
			@Override
			public String evaluate(INamedElement obj) {
				return obj == null ? null : obj.getFullyQualifiedName();
			}
		};
	}
	
	public static void checkResults(Collection<? extends INamedElement> originaResults, String[] expectedProposalsArr) {
		LinkedList<INamedElement> results = new LinkedList<>(originaResults);
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