package mmrnmhrm.core.codeassist;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import org.eclipse.dltk.core.CompletionProposal;
import org.eclipse.dltk.core.CompletionRequestor;
import org.eclipse.dltk.core.ISourceModule;
import org.junit.Test;

import dtool.ast.definitions.DefUnit;
import dtool.tests.ref.cc.ModuleSourceTestUtils;

import mmrnmhrm.tests.BaseDeeTest;
import mmrnmhrm.tests.SampleMainProject;

public class DeeCompletionEngine_Test extends BaseDeeTest {
	
	@Test
	public void testCompletionOnOutSrc() throws Exception { testCompletionOnOutSrc$(); }
	public void testCompletionOnOutSrc$() throws Exception {
		ISourceModule srcModule = SampleMainProject.getSourceModule(OutsideBuildpathTestResources.TEST_OUTFILE);
		
		srcModule.becomeWorkingCopy(null, null);
		try {
			final int offset = srcModule.getSource().indexOf("Foo foo");
			
			class CompletionRequestorTestCheck extends CompletionRequestor {
				@Override
				public void accept(CompletionProposal proposal) {
					assertTrue(proposal.getCompletionLocation() == offset);
					assertTrue(proposal.getReplaceStart() == offset);
					
					if(((DefUnit) proposal.getExtraInfo()).getModuleNode() != null) {
						assertTrue(proposal.getModelElement() != null);
					}
				}
			}
			
			CompletionRequestor requestor = new CompletionRequestorTestCheck();
			DeeCompletionEngine completionEngine = new DeeCompletionEngine();
			completionEngine.setRequestor(requestor);
			completionEngine.complete(ModuleSourceTestUtils.moduleToIModuleSource(srcModule), offset, 0);
		} finally {
			srcModule.discardWorkingCopy();
		}
	}
	
}