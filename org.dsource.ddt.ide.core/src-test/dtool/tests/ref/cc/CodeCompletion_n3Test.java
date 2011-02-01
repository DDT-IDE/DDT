package dtool.tests.ref.cc;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import org.junit.Test;

import dtool.contentassist.CompletionSession;
import dtool.refmodel.PrefixDefUnitSearch;

public class CodeCompletion_n3Test extends CodeCompletion__Common {
	
	public static final String TEST_SRCFILE = "testCodeCompletion3.d";
	
	public CodeCompletion_n3Test() {
		super(TEST_SRCFILE);
	}
	
	
	/* ------------- Tests -------------  */
	
	@Test
	public void test_ImpSelection() throws Exception {
		testComputeProposalsWithRepLen(getMarkerEndOffset("/+CC1@+/")+1, 1, 10, false,
				"ampleClass", "ampleClassB"
		);
		
		testComputeProposalsWithRepLen(getMarkerEndOffset("/+CC2@+/")+1, 1, 11, false,
				"ampleClass", "ampleClassB"
		);
	}
	
	@Test
	public void test_impModuleRef() throws Exception {
		int cc3Offset = getMarkerEndOffset("/+CC3@+/");
		
		String[] cc3results1 = array(
				"ack.mod1", "ack.mod2", "ack.mod3", "ack.modSyntaxErrors", 
				"ack.sample", "ack.sample2", "ack.sample3", 
				"ack.testSelfImport3",
				"ack.subpack.mod3", "ack.subpack.mod4",
				"hobos",
				"ack2.fooprivate", "ack2.foopublic", "ack2.foopublic2"
		);
		
		testComputeProposalsWithRepLen(cc3Offset+1, 1, 8, false, cc3results1);
		
		String[] cc3results5 = array(
				"mod1", "mod2", "mod3", "modSyntaxErrors", 
				"sample", "sample2", "sample3", 
				"testSelfImport3",
				"subpack.mod3", "subpack.mod4"
		);
		testComputeProposalsWithRepLen(cc3Offset+5, 5, 4, false, cc3results5);
		
		testComputeProposalsWithRepLen(cc3Offset+6, 6, 3, false,
				"od1", "od2", "od3", "odSyntaxErrors"
		);
		
		testComputeProposalsWithRepLen(getMarkerEndOffset("/+CC4@+/")+5, 5, 0, false, cc3results5);
	}
	
	@Test
	public void test_impModuleRef_completionTextParameters() throws Exception {
		CompletionSession session = new CompletionSession();
		PrefixDefUnitSearch search = PrefixDefUnitSearch.doCompletionSearch(getMarkerEndOffset("/+CC4@+/")+5, 
				srcModule, srcModule.getSource(), session, new DefUnitArrayListCollector());
		assertTrue(search.searchOptions.rplLen == 0);
	}
	
}

