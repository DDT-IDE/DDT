package dtool.tests.ref.cc;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import org.junit.Test;

import dtool.resolver.DefUnitCollector;
import dtool.resolver.PrefixDefUnitSearch;

public class CodeCompletion_OnModuleRefsTest extends CodeCompletion__Common {
	
	public static final String TEST_SRCFILE = "testCodeCompletion_onModuleRefs.d";
	
	public CodeCompletion_OnModuleRefsTest() {
		super(TEST_SRCFILE);
	}
	
	
	/* ------------- Tests -------------  */
	
	@Test
	public void test_ImpSelection() throws Exception {
		testComputeProposalsWithRepLen(getMarkerEndOffset("/+CC1@+/")+1, 1, 10,
				"SampleClass", "SampleClassB"
		);
		
		testComputeProposalsWithRepLen(getMarkerEndOffset("/+CC2@+/")+1, 1, 11,
				"SampleClass", "SampleClassB"
		);
	}
	
	@Test
	public void test_impModuleRef() throws Exception { test_impModuleRef$(); }
	public void test_impModuleRef$() throws Exception {
		int cc3Offset = getMarkerEndOffset("/+CC3@+/");
		int cc3xOffset = getMarkerEndOffset("/+CC3x@+/");
		
		String[] cc3results1 = array(
				"packA.sampledefs_inpack", "packA.subpack.sampledefs_inpack",
				"pack.mod1", "pack.mod2", "pack.mod3", "pack.modSyntaxErrors", 
				"pack.sample", "pack.sample2", "pack.sample3", 
				"pack.subpack.mod3", "pack.subpack.mod4",
				"pack2.fooprivate", "pack2.foopublic", "pack2.foopublic2"
		);
		
		testComputeProposalsWithRepLen(cc3Offset+1, 1,  "ack.mod3".length(), cc3results1);
		testComputeProposalsWithRepLen(cc3xOffset+1, 1, "ack .  mod3".length(), cc3results1);
		
		String[] cc3results5 = array(
				"pack.mod1", "pack.mod2", "pack.mod3", "pack.modSyntaxErrors", 
				"pack.sample", "pack.sample2", "pack.sample3", 
				"pack.subpack.mod3", "pack.subpack.mod4"
		);
		testComputeProposalsWithRepLen(cc3Offset+5,    5, "mod3".length(), cc3results5);
		testComputeProposalsWithRepLen(cc3xOffset+5+3, 5, "mod3".length(), cc3results5);
		
		testComputeProposalsWithRepLen(cc3xOffset+5+1, 5, "  mod3".length(), cc3results5);
		
		String[] cc3results6 = array(
				"pack.mod1", "pack.mod2", "pack.mod3", "pack.modSyntaxErrors"
		);
		testComputeProposalsWithRepLen(cc3Offset+6,    6, "od3".length(), cc3results6);
		testComputeProposalsWithRepLen(cc3xOffset+6+3, 6, "od3".length(), cc3results6);
		
		testComputeProposalsWithRepLen(getMarkerEndOffset("/+CC4@+/")+5, 5, 0, cc3results5);
	}
	
	@Test
	public void test_impModuleRef_completionTextParameters() throws Exception {
		PrefixDefUnitSearch search = doCompletionSearch(getMarkerEndOffset("/+CC4@+/")+5, 
				srcModule, new DefUnitCollector());
		assertTrue(search.searchOptions.rplLen == 0);
	}
	
}