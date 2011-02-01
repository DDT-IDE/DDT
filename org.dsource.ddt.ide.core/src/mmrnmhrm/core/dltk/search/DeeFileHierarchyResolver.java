package mmrnmhrm.core.dltk.search;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.IFileHierarchyInfo;
import org.eclipse.dltk.core.IFileHierarchyResolver;
import org.eclipse.dltk.core.ISourceModule;

public class DeeFileHierarchyResolver implements IFileHierarchyResolver {
	
	public DeeFileHierarchyResolver() {
	}
	
	// Don't filter files yet
	
	@Override
	public IFileHierarchyInfo resolveDown(ISourceModule file, IProgressMonitor monitor) {
		return null;
	}
	
	@Override
	public IFileHierarchyInfo resolveUp(ISourceModule file, IProgressMonitor monitor) {
		return null;
	}
	
}
