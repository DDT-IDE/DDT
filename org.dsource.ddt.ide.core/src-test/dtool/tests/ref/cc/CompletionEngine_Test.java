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
	
	protected ISourceModule srcModule;
	
	public CompletionEngine_Test() {
		String filePath = ITestResourcesConstants.TR_CA + "/" + "testCodeCompletion.d";
		IFile file = SampleMainProject.scriptProject.getProject().getFile(filePath);
		this.srcModule = DLTKCore.createSourceModuleFrom(file);
	}
	
	@Test
	public void testBasic() throws Exception { testBasic$(); }
	public void testBasic$() throws Exception {
		testCompletionEngine(getMarkerEndPos("/+CC1@+/"), 0);
		testCompletionEngine(getMarkerEndPos("/+CC2@+/")+1, 0);
		
		testCompletionEngine(getMarkerEndPos("/+CC2@+/"), 1);
		testCompletionEngine(getMarkerEndPos("/+CC3@+/"), 3);
		testCompletionEngine(getMarkerEndPos("/+CC3@+/")+1, 2);
	}
	
	protected CompletionRequestor testCompletionEngine(final int offset, final int rplLen) throws ModelException {
		CompletionRequestor requestor = new CompletionRequestor() {
			@Override
			public void accept(CompletionProposal proposal) {
				assertTrue(proposal.getCompletionLocation() == offset);
				assertTrue(proposal.getReplaceStart() == offset);
				assertTrue(proposal.getReplaceEnd() - proposal.getReplaceStart() == rplLen);
			}
		};
		DeeCompletionEngine completionEngine = new DeeCompletionEngine();
		completionEngine.setRequestor(requestor);
		completionEngine.complete(moduleToIModuleSource(srcModule), offset, 0);
		return requestor;
	}
	
	protected int getMarkerEndPos(String markerString) throws ModelException {
		int startPos = srcModule.getSource().indexOf(markerString);
		assertTrue(startPos >= 0);
		return startPos + markerString.length();
	}
	
}