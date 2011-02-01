package mmrnmhrm.tests;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import mmrnmhrm.core.CoreUtils;
import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.model.ModelUtil;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dltk.core.ModelException;


public class DeeCoreTestUtils {
	
	public static final String TESTDATA = "testdata/";
	
	public static void createSrcFolderInProject(String bundleDir, IContainer destFolder) 
			throws CoreException, URISyntaxException, IOException, ModelException {
		copyDeeCoreDirToWorkspace(bundleDir, destFolder);
		ModelUtil.addSourceFolder(destFolder, null);
	}
	
	protected static IResourceVisitor vcsFilter = new IResourceVisitor() {
		@Override
		public boolean visit(IResource resource) throws CoreException {
			return !(resource.getType() == IResource.FOLDER && resource.getName().equals(".svn"));
		}
	};
	
	
	static void copyDeeCoreDirToWorkspace(final String srcPath, final IContainer destFolder) 
			throws CoreException, URISyntaxException, IOException {
		String pluginId = DeeCore.PLUGIN_ID;
		String basePath = DeeCoreTestUtils.TESTDATA;
		copyBundleDirToWorkspace(pluginId, destFolder, new Path(basePath + srcPath));
	}
	
//	static void copyDToolDirToWorkspace(final String srcPath, final IContainer destFolder) 
//			throws CoreException, URISyntaxException, IOException {
//		String pluginId = DeeCore.PLUGIN_ID;
//		String basePath = ITestDataConstants.TESTDATA;
//		copyBundleDirToWorkspace(pluginId, destFolder, new Path(basePath + srcPath));
//	}
	
	
	public static void copyBundleDirToWorkspace(String bundleId, final IContainer destFolder, IPath bundlesrcpath) 
			throws CoreException, IOException {
		URL sourceURL = FileLocator.find(Platform.getBundle(bundleId), bundlesrcpath, null);
		assertNotNull(sourceURL);
		
		URI uri = getURI_Assured(FileLocator.toFileURL(sourceURL));
		CoreUtils.copyURLResourceToWorkspace(uri, destFolder, vcsFilter);
	}
	
	/** Return a URI for given url, which must comply to RFC 2396. */
	private static URI getURI_Assured(URL url) {
		try {
			return url.toURI();
		} catch (URISyntaxException e) {
			throw assertFail();
		}
	}
	
	public static void copyURLResourceToWorkspace(URI uri, final IContainer destFolder) throws CoreException {
		CoreUtils.copyURLResourceToWorkspace(uri, destFolder, vcsFilter);
	}
	
}
