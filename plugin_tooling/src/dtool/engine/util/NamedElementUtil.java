/*******************************************************************************
 * Copyright (c) 2012 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package dtool.engine.util;

import melnorme.lang.tooling.ast.SourceElement;
import melnorme.lang.tooling.symbols.INamedElement;
import dtool.ast.definitions.EArcheType;

public class NamedElementUtil {
	
	public static String getElementTypedLabel(INamedElement namedElement) {
		return getElementTypedLabel(namedElement, false);
	}
	
	/**
	 * Return a name identifying this defUnit in the projects source code.
	 * It's similar to a fully qualified name, but has some more information on the name about
	 * the containing defunits.
	 * (the name is not enough to uniquely locate a defUnit in a project. That's the goal anyways)
	 */
	public static String getElementTypedLabel(INamedElement namedElement, boolean useExtendedName) {
		switch(namedElement.getArcheType()) {
		case Package:
			return namedElement.getFullyQualifiedName() + "/";
		default:
		}
		return getElementTypeLabelBase(namedElement, useExtendedName);
	}
	
	public static String getElementTypeLabelBase(INamedElement namedElement, boolean useExtendedName) {
		if(namedElement.getArcheType() == EArcheType.Module) {
			return namedElement.getModuleFullName() + "/";
		}
		
		if(namedElement.isBuiltinElement()) { 
			return NATIVES_ROOT + namedElement.getName();
		}
		
		INamedElement parentNamespace = namedElement.getParentNamespace();
		
		String qualification = "";
		if(parentNamespace != null) {
			String sep = parentNamespace.getArcheType() == EArcheType.Module  ? "" : ".";
			String parentQualifedName = getElementTypeLabelBase(parentNamespace, useExtendedName);
			qualification = parentQualifedName + sep;
		}
		
		return qualification + (useExtendedName ? namedElement.getExtendedName() : namedElement.getName());
	}
	
	public static String NATIVES_ROOT = "/";
	
	public static String namedElementToString(INamedElement namedElement) {
		if(namedElement instanceof SourceElement) {
			SourceElement sourceElement = (SourceElement) namedElement;
			return sourceElement.toStringAsCode();
		} else {
			return namedElement.toString();
		}
	}
	
}
