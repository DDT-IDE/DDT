package dtool.refmodel.pluginadapters;

import org.eclipse.dltk.core.ISourceModule;

/** Temporary kludge for plugin DTOOL to access CompilationUnit's. */
public interface IGenericCompilationUnit {
	
	ISourceModule getModuleUnit();

}
