package mmrnmhrm.core.engine;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.IOException;

import melnorme.lang.ide.core.LangCore;
import melnorme.lang.ide.core.tests.CommonCoreTest;
import mmrnmhrm.tests.ITestResourcesConstants;
import mmrnmhrm.tests.SampleMainProject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.junit.Test;

// These tests could be expanded
public abstract class CompletionEngine_Test extends CommonCoreTest {
	
	protected IFile file;
	
	public CompletionEngine_Test() {
		String filePath = ITestResourcesConstants.TR_CA + "/" + "testCodeCompletion.d";
		file = SampleMainProject.project.getFile(filePath);
	}
	
	protected String getSource() throws CoreException {
		try {
			return readFileContents(file);
		} catch(IOException e) {
			throw LangCore.createCoreException("IO", e);
		}
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
	
	protected abstract void testCompletionEngine(final int offset, final int rplLen) throws Exception;
	
	protected int getMarkerEndPos(String markerString) throws CoreException {
		int startPos = getSource().indexOf(markerString);
		assertTrue(startPos >= 0);
		return startPos + markerString.length();
	}

}