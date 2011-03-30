package dtool.tests.ref.cc;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import mmrnmhrm.core.codeassist.DeeCompletionEngine;
import mmrnmhrm.tests.BaseDeeCoreTest;
import mmrnmhrm.tests.ITestResourcesConstants;
import mmrnmhrm.tests.SampleMainProject;

import org.eclipse.core.resources.IFile;
import org.eclipse.dltk.core.CompletionProposal;
import org.eclipse.dltk.core.CompletionRequestor;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.junit.Test;

// These tests could be expanded
public class CompletionEngine_Test extends BaseDeeCoreTest {
	
	protected IFile file;
	protected ISourceModule srcModule;
	
	public CompletionEngine_Test() {
		String filePath = ITestResourcesConstants.TR_CA + "/" + "testCodeCompletion.d";
		this.file = SampleMainProject.scriptProject.getProject().getFile(filePath);
		this.srcModule = DLTKCore.createSourceModuleFrom(file);
	}
	
	@Test
	public void testBasic() throws Exception { testBasic$(); }
	public void testBasic$() throws Exception {
		testCompletionEngine("/+CC1@+/");
		testCompletionEngine("/+CC2@+/");
		testCompletionEngine("/+CC3@+/");
	}

	protected CompletionRequestor testCompletionEngine(String markerStr) throws ModelException {
		final int offset = getOffsetOfMarker(markerStr);
		CompletionRequestor requestor = new CompletionRequestor() {
			@Override
			public void accept(CompletionProposal proposal) {
				assertTrue(proposal.getReplaceStart() == offset);
				assertTrue(proposal.getReplaceEnd() - proposal.getReplaceStart() == 0);
			}
		};
		DeeCompletionEngine completionEngine = new DeeCompletionEngine();
		completionEngine.setRequestor(requestor);
		completionEngine.complete(moduleToIModuleSource(srcModule), offset, 0);
		return requestor;
	}
	
	protected int getOffsetOfMarker(String markerString) throws ModelException {
		int indexOf = srcModule.getSource().indexOf(markerString);
		assertTrue(indexOf >= 0);
		return indexOf;
	}

}