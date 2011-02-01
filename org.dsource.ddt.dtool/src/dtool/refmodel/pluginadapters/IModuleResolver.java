package dtool.refmodel.pluginadapters;

import dtool.ast.definitions.Module;

/** 
 * Adapter interface for plugin DTool to interface with a host plugin that 
 * knows how to find modules (used by the IDE plugins for example)
 */
public interface IModuleResolver {

	/** Finds a module with the given Fully Quallified name.
	 * @param sourceRefModule The module where the reference originates.
	 * @param packageNames The name of packages of the module to find.
	 * @param module The name of the modules to find. */
	Module findModule(Module sourceRefModule, String[] packages, String module) throws Exception;

	
	/** Determines a list of names of modules whose FQ-name starts with the
	 * given FQ-name. */
	String[] findModules(Module refSourceModule, String fqName) throws Exception;
 
}
