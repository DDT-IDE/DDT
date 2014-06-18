package dtool.tests.ref.cc;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import mmrnmhrm.core.engine_client.DToolClient;
import mmrnmhrm.tests.ITestResourcesConstants;
import mmrnmhrm.tests.SampleMainProject;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.IBuffer;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.junit.After;

import dtool.resolver.CompareDefUnits;
import dtool.resolver.PrefixDefUnitSearch;
import dtool.resolver.api.ECompletionResultStatus;
import dtool.tests.CommonDToolTest;

public class CodeCompletion__Common extends CommonDToolTest {
	
	protected final ISourceModule srcModule;
	protected ICodeCompletionTester ccTester;
	
	public CodeCompletion__Common(String testFilePath) {
		this(SampleMainProject.getSourceModule(ITestResourcesConstants.TR_CA, testFilePath));
	}
	
	public CodeCompletion__Common(ISourceModule srcModule) {
		this.srcModule = srcModule;
		this.ccTester = new ICodeCompletionTester() {
			@Override
			public void testComputeProposalsWithRepLen(int repOffset, int prefixLen,
					int repLen, String... expectedProposals) throws CoreException {
				testComputeProposalsDo(repOffset, repLen, expectedProposals);
			}
			
			@Override
			public void runAfters() {
			}
		};
	}
	
	@After
	public void runAfters() {
		ccTester.runAfters();
	}
	
	protected int getMarkerEndOffset(String marker) throws ModelException {
		return srcModule.getSource().indexOf(marker) + marker.length();
	}
	
	protected int getMarkerStartOffset(String marker) throws ModelException {
		return srcModule.getSource().indexOf(marker);
	}
	
	protected IBuffer getDocument() throws ModelException {
		return srcModule.getBuffer();
	}
	
	protected PrefixDefUnitSearch testUnavailableCompletion(int offset, ECompletionResultStatus caResult) 
			throws CoreException {
		PrefixDefUnitSearch search = DToolClient.getDefault().doCodeCompletion(srcModule, offset);
		assertTrue(search.getResultCode() == caResult);
		return search;
	}
	
	public void testComputeProposalsWithRepLen(int repOffset, int prefixLen, int repLen, 
		String... expectedProposals) throws CoreException {
		ccTester.testComputeProposalsWithRepLen(repOffset, prefixLen, repLen, expectedProposals);
	}
	
	public final void testComputeProposals(int repOffset, int prefixLen, 
			String... expectedProposals) throws CoreException {
		testComputeProposalsWithRepLen(repOffset, prefixLen, 0, expectedProposals);
	}
	
	
	protected void testComputeProposalsDo(int repOffset, int repLen, String[] expectedProposals) 
		throws CoreException {
		
		PrefixDefUnitSearch completionSearch = DToolClient.getDefault().doCodeCompletion(srcModule, repOffset);
		
		if(expectedProposals == null) {
			assertTrue(completionSearch.getResultCode() != ECompletionResultStatus.RESULT_OK);
		} else {
			assertTrue(completionSearch.getResultCode() == ECompletionResultStatus.RESULT_OK);
			assertTrue(completionSearch.searchOptions.rplLen == repLen);
			
			CompareDefUnits.checkResults(completionSearch.getResults(), expectedProposals);
		}
	}
	
}