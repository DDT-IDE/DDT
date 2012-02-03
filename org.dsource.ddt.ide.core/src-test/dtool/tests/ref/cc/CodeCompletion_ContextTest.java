package dtool.tests.ref.cc;


import org.junit.Test;

import dtool.contentassist.CompletionSession.ECompletionSessionResults;
import dtool.tests.DToolTests;

public class CodeCompletion_ContextTest extends CodeCompletion__Common {
	
	public static final String TEST_SRCFILE = "testCA_contexts.d";
	
	public CodeCompletion_ContextTest() {
		super(TEST_SRCFILE);
	}
	
	String[] topLevelResults = array(
			"Foo", "foo", 
			"pack", "sampleVar", "sampleVarB", "SampleClass", "SampleClassB", "i345u", 
			"pack2", "foopublicImportVar0", "foopublicImportVar", "foopublicImportVar2", 
			"othervar", "Other", "testCA_contexts"
	);
	
	/* ------------- Tests -------------  */
	
	@Test
	public void test2() throws Exception {
		testComputeProposalsWithRepLen(getMarkerEndOffset("/+CC2@+/"), 0, 4, true,
				topLevelResults
		);
	}
	
	@Test
	public void test3() throws Exception {
		testUnavailableCompletion(getMarkerEndOffset("/+CC3i@+/"), ECompletionSessionResults.INVALID_LOCATION_INSCOPE);
		testUnavailableCompletion(getMarkerEndOffset("/+CC4i+/"), ECompletionSessionResults.INVALID_LOCATION_INSCOPE);
		if(DToolTests.UNSUPPORTED_FUNCTIONALITY_MARKER) {
			testUnavailableCompletion(getMarkerEndOffset("/+CC5i+/"), ECompletionSessionResults.INVALID_LOCATION_INSCOPE);
		}
		
		testComputeProposalsWithRepLen(getMarkerStartOffset("/+CC3+/"), 0, 0, true, topLevelResults);
		testComputeProposalsWithRepLen(getMarkerStartOffset("/+CC4+/"), 0, 0, true, topLevelResults);
		testComputeProposalsWithRepLen(getMarkerStartOffset("/+CC5+/"), 0, 0, true, topLevelResults);
	}
	
	@Test
	public void test4() throws Exception {
		testComputeProposalsWithRepLen(getMarkerEndOffset("/+CC9@+/"), 0, 3, true,
				topLevelResults
		);
	}
	
}

