package mmrnmhrm.core.engine;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import melnorme.lang.ide.core.tests.CommonCoreTest;
import melnorme.utilbox.misc.IByteSequence;
import melnorme.utilbox.misc.StreamUtil;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

import dtool.engine.ModuleParseCache_Test;

/**
 * Module resolver helper for the {@link CoreResolverSourceTests} fixture
 */
public class TestsProjectFileOverlay {
	
	protected final IFile overlayedFile;
	protected final IByteSequence overlayedFilePreviousContents;
	
	public TestsProjectFileOverlay(IProject project, String moduleName, String source) 
		throws IOException, CoreException {
		
		Path filePath = new Path(moduleName.replaceAll("\\.", "/") + ".d");
		IContainer srcFolder = project.getFolder("src-dtool");
		if(!srcFolder.exists()) {
			srcFolder = project;
		}
		overlayedFile = srcFolder.getFile(filePath);
		
		if(overlayedFile.exists()) {
			overlayedFilePreviousContents = StreamUtil.readAllBytesFromStream(overlayedFile.getContents());
		} else {
			overlayedFilePreviousContents = null;
		}
		ModuleParseCache_Test.writeToFileAndUpdateMTime(CommonCoreTest.path(overlayedFile.getLocation()), source);
		overlayedFile.refreshLocal(0, null);
	}
	
	public void cleanupChanges() {
		try {
			doCleanupChanges();
		} catch(CoreException e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e);
		}
	}
	
	public void doCleanupChanges() throws CoreException {
		if(overlayedFile != null) {
			if(overlayedFilePreviousContents == null) {
				overlayedFile.delete(false, null);
			} else {
				ByteArrayInputStream is = new ByteArrayInputStream(overlayedFilePreviousContents.toByteArray());
				overlayedFile.setContents(is, IResource.NONE, null);
			}
		}
	}
	
}