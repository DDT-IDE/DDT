/*******************************************************************************
 * Copyright (c) 2008, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package org.dsource.ddt.ide.core.model;

import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;

import dtool.ast.ASTNeoNode;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.Definition;
import dtool.ast.definitions.DefinitionFunction;
import dtool.ast.definitions.DefinitionVariable;
import dtool.ast.definitions.Module;

public class SourceModelUtil {
	
	/** Get's a type handle for an AST node.
	 * If the node is not element-like, use the nearest enclosing node. */
	public static IMember getTypeHandle(ASTNeoNode node) {
		
		while(!(isTypeHandleNode(node))) {
			node = node.getParent();
		}
		return getTypeHandle((DefUnit) node);
	}
	
	// XXX: This needs to be redone
	@Deprecated
	public static IMember getTypeHandle(DefUnit defunit) {
		
		if(defunit instanceof Module) {
			Module mod = (Module) defunit;
			ISourceModule moduleUnit = mod.getModuleNode().getModuleUnit();
			return moduleUnit.getType(defunit.getName());
		}
		IMember member = getTypeHandle(defunit.getParent());
		
		// If the top element doesn't support children elements:
		if(!(member instanceof IType))
			return member;
		
		IType topType = (IType) member;
		Definition def = (Definition) defunit;
		if (defunit instanceof DefinitionVariable) {
			return topType.getField(def.toStringAsElement());
		} else if (defunit instanceof DefinitionFunction) {
			//return topType.getMethod(def.toStringAsElement());
			return topType.getMethod(def.getName());
		} else {
			return topType.getType(def.toStringAsElement());
		}
	}
	
	private static boolean isTypeHandleNode(ASTNeoNode node) {
		return node instanceof Definition || node instanceof Module;
	}
	
}
