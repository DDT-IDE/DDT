package mmrnmhrm.core.engine_client;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.nio.file.Path;
import java.util.ArrayList;

import melnorme.lang.ide.core.tests.CommonCoreTest;
import mmrnmhrm.core.engine_client.DeeCompletionEngine.RefSearchCompletionProposal;
import mmrnmhrm.tests.IOutsideBuildpathTestResources;
import mmrnmhrm.tests.ITestResourcesConstants;
import mmrnmhrm.tests.SampleMainProject;

import org.eclipse.core.resources.IFile;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.core.CompletionProposal;
import org.eclipse.dltk.core.CompletionRequestor;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.junit.Test;

import dtool.ast.definitions.INamedElement;
import dtool.tests.MockCompilerInstalls;

// These tests could be expanded
public class CompletionEngine_Test extends CommonCoreTest {
	
	protected ISourceModule srcModule;
	
	public CompletionEngine_Test() {
		String filePath = ITestResourcesConstants.TR_CA + "/" + "testCodeCompletion.d";
		IFile file = SampleMainProject.scriptProject.getProject().getFile(filePath);
		this.srcModule = DLTKCore.createSourceModuleFrom(file);
	}
	
	@Test
	public void testBasic() throws Exception { testBasic$(); }
	public void testBasic$() throws Exception {
		assertFail();
		testCompletionEngine(getMarkerEndPos("/+CC1@+/"), 0);
		testCompletionEngine(getMarkerEndPos("/+CC2@+/")+1, 0);
		
		testCompletionEngine(getMarkerEndPos("/+CC2@+/"), 1);
		testCompletionEngine(getMarkerEndPos("/+CC3@+/"), 3);
		testCompletionEngine(getMarkerEndPos("/+CC3@+/")+1, 2);
	}
	
	protected void testCompletionEngine(final int offset, final int rplLen) throws ModelException {
		testCompletionEngine((IModuleSource) srcModule, offset, rplLen,
			MockCompilerInstalls.DEFAULT_DMD_INSTALL_EXE_PATH);
	}
	
	public static DeeCompletionEngine testCompletionEngine(IModuleSource moduleSource, final int offset,
		final int rplLen, final Path compilerPath) {
		CompletionEngineTestsRequestor requestor = new CompletionEngineTestsRequestor(offset, rplLen);
		DeeCompletionEngine completionEngine = new DeeCompletionEngine() {
			@Override
			protected Path getCompilerPath(IModuleSource moduleSource) {
				return compilerPath;
			}
		};
		completionEngine.setRequestor(requestor);
		completionEngine.complete(moduleSource, offset, 0);
		return completionEngine;
	}
	
	protected int getMarkerEndPos(String markerString) throws ModelException {
		int startPos = srcModule.getSource().indexOf(markerString);
		assertTrue(startPos >= 0);
		return startPos + markerString.length();
	}
	
	public static final class CompletionEngineTestsRequestor extends CompletionRequestor {
		protected final int offset;
		protected final int rplLen;
		protected final ArrayList<INamedElement> results = new ArrayList<>();
		
		private CompletionEngineTestsRequestor(int offset, int rplLen) {
			this.offset = offset;
			this.rplLen = rplLen;
		}
		
		@Override
		public void accept(CompletionProposal proposal) {
			assertTrue(proposal instanceof RefSearchCompletionProposal);
			RefSearchCompletionProposal refProposal = (RefSearchCompletionProposal) proposal;
			
			assertTrue(proposal.getCompletionLocation() == offset);
			assertTrue(proposal.getReplaceStart() == offset);
			assertTrue(proposal.getReplaceEnd() - proposal.getReplaceStart() == rplLen);
			INamedElement defUnit = refProposal.getExtraInfo();
			results.add(defUnit);
		}
	}
	
	@Test
	public void testCompletionOnOutSrc() throws Exception { testCompletionOnOutSrc$(); }
	public void testCompletionOnOutSrc$() throws Exception {
		ISourceModule srcModule = SampleMainProject.getSourceModule(IOutsideBuildpathTestResources.TEST_OUTFILE);
		
		srcModule.becomeWorkingCopy(null, null);
		try {
			final int offset = srcModule.getSource().indexOf("Foo foo");
			
			class CompletionRequestorTestCheck extends CompletionRequestor {
				@Override
				public void accept(CompletionProposal proposal) {
					assertTrue(proposal.getCompletionLocation() == offset);
					assertTrue(proposal.getReplaceStart() == offset);
				}
			}
			
			DeeCompletionEngine completionEngine = new DeeCompletionEngine();
			completionEngine.setRequestor(new CompletionRequestorTestCheck());
			completionEngine.complete((IModuleSource) srcModule, offset, 0);
		} finally {
			srcModule.discardWorkingCopy();
		}
	}
	
}