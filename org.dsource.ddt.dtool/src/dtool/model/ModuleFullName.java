/*******************************************************************************
 * Copyright (c) 2014, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.model;

import static melnorme.utilbox.core.CoreUtil.areEqual;

/**
 * A fully qualified name of module;
 */
public class ModuleFullName {
	
	protected final String moduleFullName;
	
	public ModuleFullName(String moduleFullName) {
		this.moduleFullName = moduleFullName;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(!(obj instanceof ModuleFullName)) return false;
		
		ModuleFullName other = (ModuleFullName) obj;
		
		return areEqual(moduleFullName, other.moduleFullName);
	}
	
	@Override
	public int hashCode() {
		return moduleFullName.hashCode();
	}
	
	@Override
	public String toString() {
		return "[" + moduleFullName + "]";
	}
	
}