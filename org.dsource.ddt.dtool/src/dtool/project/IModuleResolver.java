/*******************************************************************************
 * Copyright (c) 2013, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.project;

import dtool.ast.definitions.Module;

/** 
 * An interface for a service that given module names knows how to find parsed Module's in an 
 * underlying context (for example the modules in the buildpath of a project). 
 */
public interface IModuleResolver {
	
	/** Finds a module with the given fully qualified name.
	 * @param packages The packages of the module to find.
	 * @param module The name of the modules to find. 
	 * @return the respective module or null if not found
	 */
	Module findModule(String[] packages, String module) throws Exception;
	
	
	/** Searches for the names of modules whose fully qualified names start with the given fqNamePrefix.
	 * @return an array of fully qualified names of the found modules. */
	String[] findModules(String fqNamePrefix) throws Exception;
	
}