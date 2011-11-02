package mmrnmhrm.tests;

import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.projectmodel.ProjectModelUtil;
import mmrnmhrm.tests.utils.ResourceUtils;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;


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
	
}
