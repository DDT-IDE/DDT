package dtool.tests.ref.cc;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import org.junit.Test;

import dtool.contentassist.CompletionSession.ECompletionSessionResults;
import dtool.refmodel.PrefixDefUnitSearch;

public class CodeCompletion_UnavailableTest extends CodeCompletion__Common {
	
	public static final String TEST_SRCFILE = "testCodeCompletion.d";
	
	public CodeCompletion_UnavailableTest() {
		super(TEST_SRCFILE);
	}
	
	@Test
	public void test1() throws Exception {
		testUnavailableCompletion(getMarkerEndOffset("/+CC1@+/")-1, 
				ECompletionSessionResults.INVALID_LOCATION_INTOKEN);
	}
	
	@Test
	public void test2() throws Exception {
		testUnavailableCompletion(getMarkerEndOffset("/+CC9@+/")+5, 
				ECompletionSessionResults.INVALID_LOCATION_INTOKEN);
	}
	
	@Test
	public void test3() throws Exception {
		testUnavailableCompletion(getMarkerEndOffset("/+CC10+/")+4, 
				ECompletionSessionResults.WEIRD_LOCATION_REFQUAL);
		
		PrefixDefUnitSearch search = testUnavailableCompletion(getMarkerEndOffset("/+CC10+/")+6, 
				ECompletionSessionResults.RESULT_OK);
		
		assertTrue(search.searchOptions.namePrefixLen == 0 && search.searchOptions.rplLen == 0);
		assertTrue(search.searchOptions.searchPrefix.isEmpty());
	}
	
}
