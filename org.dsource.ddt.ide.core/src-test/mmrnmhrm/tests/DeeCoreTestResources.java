package mmrnmhrm.tests;

import java.io.File;
import java.io.IOException;

import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.projectmodel.ProjectModelUtil;
import mmrnmhrm.tests.utils.BundleResourcesUtil;
import mmrnmhrm.tests.utils.ResourceUtils;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

import dtool.tests.DToolTestResources;


public class DeeCoreTestResources {
	
	private static final String TESTDATA_BUNDLE_PATH = "testdata/";
	
	public static <T extends IContainer> T createSrcFolderFromDeeCoreResource(String resourcePath, T destFolder) 
			throws CoreException {
		createWorkspaceFolderFromDeeResource(resourcePath, destFolder);
		ProjectModelUtil.addSourceFolder(destFolder, null);
		return destFolder;
	}
	
	public static <T extends IContainer> T createWorkspaceFolderFromDeeResource(String resourcePath, T destFolder) 
			throws CoreException {
		String pluginId = DeeCore.PLUGIN_ID;
		String basePath = DeeCoreTestResources.TESTDATA_BUNDLE_PATH;
		ResourceUtils.copyBundleDirToWorkspace(pluginId, new Path(basePath).append(resourcePath), destFolder);
		return destFolder;
	}
	
	
	/**
	 * Copies the contents of a bundle resource folder into the tests working dir 
	 */
	public static void copyTestFolderContentsFromDeeResource(String resourcePath, String destFolderPath) throws CoreException {
		
		File destFolder = new File(DToolTestResources.getWorkingDir(), destFolderPath);		
		
		try {
			String resourceBundlePath = new Path(TESTDATA_BUNDLE_PATH).append(resourcePath).toString();
			BundleResourcesUtil.copyDirContents(DeeCore.PLUGIN_ID, resourceBundlePath, destFolder);
		} catch(IOException e) {
			DeeCore.createCoreException("Error while copying contents of bundle resources to " + destFolder, e);
		}
	}

	public static File getTestResource(String relativePath) {
		return new File(DToolTestResources.getWorkingDir(), relativePath);
	}
	
}
