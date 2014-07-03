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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;
import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import static melnorme.utilbox.core.CoreUtil.areEqualArrays;
import melnorme.utilbox.misc.ArrayUtil;
import melnorme.utilbox.misc.StringUtil;

/**
 * A fully qualified name of a module.
 */
public class ModuleFullName {
	
	public static final String NAME_SEP = ".";
	
	protected final String[] segments;
	protected final String moduleFullNameString; //cached
	
	public ModuleFullName(String moduleFullName) {
		this(moduleFullName, StringUtil.splitString(moduleFullName, NAME_SEP.charAt(0)));
	}
	
	/** Note: the new class will own segments array, it should not be modified. */
	public ModuleFullName(String[] segments) {
		this(StringUtil.collToString(segments, NAME_SEP), segments);
	}
	
	protected ModuleFullName(String moduleFullName, String[] segments) {
		assertTrue(segments.length > 0);
		this.moduleFullNameString = moduleFullName;
		this.segments = segments;
		
		for (String segment : segments) {
			assertNotNull(segment);
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(!(obj instanceof ModuleFullName)) return false;
		
		ModuleFullName other = (ModuleFullName) obj;
		
		return areEqualArrays(segments, other.segments);
	}
	
	@Override
	public int hashCode() {
		// We use hashcode of moduleFullName instead of using segments since it's cached.
		// This might cause colisions with segments with '.' in them, but that's a totally unimportant case.
		return moduleFullNameString.hashCode();
	}
	
	public String[] getPackages() {
		return ArrayUtil.copyFrom(segments, segments.length - 1);
	}
	
	public String getBaseName() {
		return segments[segments.length - 1];
	}
	
	public String getFullNameAsString() {
		return moduleFullNameString;
	}
	
	/* ----------------- ----------------- */
	
	@Override
	public String toString() {
		return "[" + moduleFullNameString + "]";
	}
	
}