package dtool.refmodel.pluginadapters;

import dtool.ast.definitions.Module;

/** 
 * An interface for a service that given module names knows how to find parsed Module's in an 
 * underlying context (for example the modules in the buildpath of a project). 
 */
public interface IModuleResolver {

	/** Finds a module with the given fully quallified name.
	 * @param packages The packages of the module to find.
	 * @param module The name of the modules to find. 
	 * */
	Module findModule(String[] packages, String module) throws Exception;

	
	/** Searches for the names of modules whose fully qualified names start with the given fqNamePrefix */
	String[] findModules(String fqNamePrefix) throws Exception;
 
}