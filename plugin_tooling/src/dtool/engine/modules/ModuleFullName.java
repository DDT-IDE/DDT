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
package dtool.engine.modules;

import melnorme.lang.tooling.AbstractElementName;
import melnorme.utilbox.misc.ArrayUtil;

/**
 * A fully qualified name of a module.
 */
public class ModuleFullName extends AbstractElementName {
	
	public static final String NAME_SEP = ".";
	
	public ModuleFullName(String moduleFullName) {
		super(moduleFullName, NAME_SEP);
	}
	
	/** Note: the new class will own segments array, it should not be modified. */
	public ModuleFullName(String[] segments) {
		super(segments, NAME_SEP);
	}
	
	public String[] getPackages() {
		return ArrayUtil.copyFrom(segments, segments.length - 1);
	}
	
	public String getModuleSimpleName() {
		return getLastSegment();
	}
	
}