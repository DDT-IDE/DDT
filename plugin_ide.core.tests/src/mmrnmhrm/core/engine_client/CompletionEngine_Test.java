package mmrnmhrm.core.engine_client;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import melnorme.lang.ide.core.tests.CommonCoreTest;
import mmrnmhrm.tests.IOutsideBuildpathTestResources;
import mmrnmhrm.tests.ITestResourcesConstants;
import mmrnmhrm.tests.SampleMainProject;

import org.eclipse.core.resources.IFile;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.junit.Test;

// These tests could be expanded
public abstract class CompletionEngine_Test extends CommonCoreTest {
	
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
	
	protected abstract void testCompletionEngine(final int offset, final int rplLen) throws ModelException;
	
	protected int getMarkerEndPos(String markerString) throws ModelException {
		int startPos = srcModule.getSource().indexOf(markerString);
		assertTrue(startPos >= 0);
		return startPos + markerString.length();
	}
	
	@Test
	public void testCompletionOnOutSrc() throws Exception { testCompletionOnOutSrc$(); }
	public void testCompletionOnOutSrc$() throws Exception {
		ISourceModule srcModule = SampleMainProject.getSourceModule(IOutsideBuildpathTestResources.TEST_OUTFILE);
		
		srcModule.becomeWorkingCopy(null, null);
		try {
			final int offset = srcModule.getSource().indexOf("Foo foo");
			
			testCompletionEngine(offset, 0);
			
		} finally {
			srcModule.discardWorkingCopy();
		}
	}
	
}