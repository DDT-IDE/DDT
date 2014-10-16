package mmrnmhrm.core.search;

import melnorme.lang.ide.core.utils.EclipseUtils;
import mmrnmhrm.core.DeeCore;

import org.dsource.ddt.ide.core.DeeNature;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ModelException;

public class ImportPathVisitor {
	
	protected static boolean isAccessible(IScriptProject scriptProject) {
		return DeeNature.isAccessible(scriptProject.getProject(), false);
	}
	
	protected void iteratonFullImportPath(IScriptProject deeProject) throws ModelException {
		if(!isAccessible(deeProject))
			return;
		
		for (IProjectFragment srcFolder : deeProject.getProjectFragments()) {
			boolean stop = visitSourceContainer(srcFolder);
			if(stop) {
				return;
			}
		}
		
		IBuildpathEntry[] resolvedBuildpath = deeProject.getResolvedBuildpath(true);
		for (IBuildpathEntry bpEntry : resolvedBuildpath) {
			if(bpEntry.getEntryKind() == IBuildpathEntry.BPE_PROJECT) {
				IPath path = bpEntry.getPath();
				if(path.segmentCount() != 1) {
					DeeCore.logError("Invalid path in project BP entry: " + path);
					continue;
				}
				String projectName = path.segment(0);
				IScriptProject depProject = getDeeScriptProject(projectName);
				if(depProject == null) {
					continue;
				}
				IProjectFragment[] projectFragments = depProject.getProjectFragments();
				for (IProjectFragment projectFragment : projectFragments) {
					if(projectFragment.isExternal()) {
						continue;
					}
					
					boolean stop = visitSourceContainer(projectFragment);
					if(stop) {
						return;
					}
				}
			}
		}
	}
	
	@SuppressWarnings("unused")
	protected boolean visitSourceContainer(IProjectFragment srcContainer) throws ModelException {
		return false;
	}
	
	protected IScriptProject getDeeScriptProject(String projectName) {
		IProject project = EclipseUtils.getWorkspaceRoot().getProject(projectName);
		try {
			if(DeeNature.isAccessible(project)) {
				return DLTKCore.create(project);
			}
		} catch (CoreException e) {
		}
		return null;
	}
	
}