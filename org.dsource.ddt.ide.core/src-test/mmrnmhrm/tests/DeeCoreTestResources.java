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
	
	public static void createSrcFolderInProject(String bundleDir, IContainer destFolder) 
			throws CoreException, IOException {
		copyDeeCoreResourceToWorkspace(bundleDir, destFolder);
		ProjectModelUtil.addSourceFolder(destFolder, null);
	}
	
	static void copyDeeCoreResourceToWorkspace(final String srcPath, final IContainer destFolder) 
			throws CoreException, IOException {
		String pluginId = DeeCore.PLUGIN_ID;
		String basePath = DeeCoreTestResources.TESTDATA;
		ResourceUtils.copyBundleDirToWorkspace(pluginId, destFolder, new Path(basePath).append(srcPath));
	}
	
}
