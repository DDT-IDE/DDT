package mmrnmhrm.tests.utils;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubProgressMonitor;

public class ResourceUtils {
	
	public static void createRecursive(IFolder container, boolean force) throws CoreException {
		if(!container.getParent().exists()) {
			if(container.getParent().getType() == IResource.FOLDER) {
				createRecursive((IFolder)container.getParent(), force);
			}
		}
		container.create(force, true, null);
	}
	
	public static void createRecursive(IFile file, InputStream is, boolean force) throws CoreException {
		if(!file.getParent().exists()) {
			if(file.getParent().getType() == IResource.FOLDER) {
				createRecursive((IFolder)file.getParent(), force);
			}
		}
		file.create(is, force, null);
	}
	
	public static void copyContentsOverwriting(IContainer source, IContainer destContainer, IResourceVisitor filter, 
			SubProgressMonitor monitor) throws CoreException {
		assertTrue(destContainer.exists());
		
		IResource[] members = source.members();
		for (int i = 0; i < members.length; i++) {
			IResource srcResource = members[i];
			IResource dstResource = destContainer.findMember(srcResource.getName());
			
			if (!filter.visit(srcResource))
				continue;
			
			if(srcResource.getType() == IResource.FILE) {
				if(dstResource != null) {
					dstResource.delete(true, monitor);
				}
				IPath destPath = destContainer.getFullPath().append(srcResource.getName());
				srcResource.copy(destPath, IResource.FORCE, monitor);
				
			} else if(srcResource.getType() == IResource.FOLDER) {
				IFolder srcFolder = (IFolder) srcResource;
				if(srcFolder.equals(destContainer)) {
					continue; // We should not copy a folder into itself, if the folder is the dest
				}
				
				IFolder dstFolder = destContainer.getFolder(new Path(srcFolder.getName()));

				if(dstResource == null) {
					dstFolder.create(true, true, monitor);
				} else if(dstResource.getType() == IResource.FILE) {
					dstResource.delete(true, monitor);
					dstFolder.create(true, true, monitor);
				} 
				
				assertTrue(dstFolder.exists());
				copyContentsOverwriting(srcFolder, dstFolder, filter, monitor);
			}
		}
	}
	
	public static void copyURLResourceToWorkspace(URI uri, final IContainer destFolder, IResourceVisitor filter) 
			throws CoreException {
		IProject tempProject = createNewProject("__temp.linkProject"); // a hack!
		IFolder linkFolder = tempProject.getFolder(new Path("__copylink")); 
		linkFolder.createLink(uri, IResource.NONE, null);
		
		if(destFolder.getType() == IResource.FOLDER) {
			IFolder newFolder = (IFolder) destFolder;
			newFolder.create(true, true, null);
		}
		copyContentsOverwriting(linkFolder, destFolder, filter, null);
		
		linkFolder.delete(false, null);
		tempProject.delete(false, null);
	}
	
	private static IProject createNewProject(String projectName) throws CoreException {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		assertTrue(!project.exists()); 
		project.create(null);
		project.open(null);
		return project;
	}
	
	public static void copyBundleDirToWorkspace(String bundleId, final IContainer destFolder, IPath bundlesrcpath)
			throws CoreException, IOException {
		URL sourceURL = FileLocator.find(Platform.getBundle(bundleId), bundlesrcpath, null);
		assertNotNull(sourceURL);
		
		URI uri = getURIFromProperURL(FileLocator.toFileURL(sourceURL));
		ResourceUtils.copyURLResourceToWorkspace(uri, destFolder, vcsFilter);
	}
	
	public static void copyURIResourceToWorkspace(URI uri, final IContainer destFolder) throws CoreException {
		ResourceUtils.copyURLResourceToWorkspace(uri, destFolder, vcsFilter);
	}
	
	protected static IResourceVisitor vcsFilter = new IResourceVisitor() {
		@Override
		public boolean visit(IResource resource) throws CoreException {
			return !(resource.getType() == IResource.FOLDER && resource.getName().equals(".svn"));
		}
	};
	
	/** Return a URI for given url, which must comply to RFC 2396. */
	public static URI getURIFromProperURL(URL validUrl) {
		try {
			return validUrl.toURI();
		} catch(URISyntaxException e) {
			throw assertFail();
		}
	}
	
}
