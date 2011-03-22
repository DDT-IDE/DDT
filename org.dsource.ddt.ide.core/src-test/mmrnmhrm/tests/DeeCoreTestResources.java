package mmrnmhrm.tests;

import java.io.IOException;

import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.projectmodel.ProjectModelUtil;
import mmrnmhrm.tests.utils.ResourceUtils;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;


public class DeeCoreTestResources {
	
	public static final String TESTDATA = "testdata/";
	
	public static <T extends IContainer> T createSrcFolderFromDeeCoreResource(String resourcePath, T destFolder) 
			throws CoreException, IOException {
		createFolderFromDeeResource(resourcePath, destFolder);
		ProjectModelUtil.addSourceFolder(destFolder, null);
		return destFolder;
	}
	
	public static <T extends IContainer> T createFolderFromDeeResource(String resourcePath, T destFolder) 
			throws CoreException, IOException {
		String pluginId = DeeCore.PLUGIN_ID;
		String basePath = DeeCoreTestResources.TESTDATA;
		ResourceUtils.copyBundleDirToWorkspace(pluginId, destFolder, new Path(basePath).append(resourcePath));
		return destFolder;
	}
	
}
