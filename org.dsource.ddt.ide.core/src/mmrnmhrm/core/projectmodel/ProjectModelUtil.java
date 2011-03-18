package mmrnmhrm.core.projectmodel;

import melnorme.utilbox.misc.ArrayUtil;
import mmrnmhrm.core.DeeCore;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;


public class ProjectModelUtil {
	
	public static IScriptProject getDeeProject(String projectName) {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		return DLTKCore.create(project);
	}
	
	/** Get's a sourceModule's file, apparently getUnderlyingResource()
	 * doesn't work all the time. */
	public static IFile getSourceModuleFile(ISourceModule srcModule) {
		//return (IFile) srcModule.getUnderlyingResource();
		return DeeCore.getWorkspaceRoot().getFile(srcModule.getPath());
	}
	
	/** Setup the given folder as a source folder. */
	public static IProjectFragment addSourceFolder(IContainer folder, IProgressMonitor pm) throws CoreException {
		IScriptProject dltkProj = DLTKCore.create(folder.getProject());
		IProjectFragment fragment = dltkProj.getProjectFragment(folder);
		if(!fragment.exists()) {
			IBuildpathEntry[] bpentries = dltkProj.getRawBuildpath();
			IBuildpathEntry entry = DLTKCore.newSourceEntry(fragment.getPath());
			dltkProj.setRawBuildpath(ArrayUtil.concat(bpentries, entry), pm);
		}
		return fragment;
	}
	
	/** Setup the given resource as a library entry. */
	public static void addLibraryEntry(IResource res, IProgressMonitor pm) throws ModelException {
		IScriptProject project = DLTKCore.create(res.getProject());
		IBuildpathEntry[] entries = project.getRawBuildpath();
		// TODO: validate new entry?
		IBuildpathEntry entry = DLTKCore.newLibraryEntry(res.getFullPath());
		project.setRawBuildpath(ArrayUtil.concat(entries, entry), pm);
	}
	
	/** Adds a nature to the given project if it doesn't exist already.*/
	public static void addNature(IProject project, String natureID) throws CoreException {
		IProjectDescription description = project.getDescription();
		String[] natures = description.getNatureIds();
		if(ArrayUtil.contains(natures, natureID))
			return;
		
		String[] newNatures = ArrayUtil.append(natures, natureID);
		description.setNatureIds(newNatures);
		project.setDescription(description, null); 
	}


	
}
