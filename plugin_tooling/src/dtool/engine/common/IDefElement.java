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
package dtool.engine.common;

import dtool.ast.definitions.INamedElement;
import dtool.engine.modules.IModuleResolver;
import dtool.resolver.CommonDefUnitSearch;


public interface IDefElement {
	
	/** The name of the element that is referred to. */
	public abstract String getName();
	
	/** @return the extended name of the element referred to. 
	 * The extended name is the name of the element/defunit plus additional adornments(can contain spaces) that
	 * allow to disambiguate this defUnit from homonym defUnits in the same scope 
	 * (for example the adornment can be function parameters for function elements).
	 */
	public abstract String getExtendedName();
	
	/** @return the name by which this element can be referred to in the normal namespace.
	 * Usually it's the same as the name, but it can be null or empty, 
	 * meaning that the element cannot be referred by name (for example constructors elements). */
	public String getNameInRegularNamespace();
	
	/** @return true if this is a pre-defined/native language element. 
	 * (example: primitives such as int, void, or native types like arrays, pointer types) 
	 */
	public abstract boolean isLanguageIntrinsic();
	
	/** @return The fully qualified name of this element. Not null. */
	public abstract String getFullyQualifiedName();
	
	/** @return the fully qualified name of the module this element belongs to. 
	 * Can be null if element is not contained in a module. */
	public abstract String getModuleFullyQualifiedName();
	
	/** @return the nearest enclosing {@link INamedElement}.
	 * For modules and packages, that is null. */
	public abstract INamedElement getParentElement();
	
	/**
	 * Resolve given search in the members scope of this defunit.
	 * Note that the members can be different from the lexical scope that a defunit may provide.
	 */
	public abstract void resolveSearchInMembersScope(CommonDefUnitSearch search);
	
	/** 
	 * Return the type of this defElement, when it is referenced as a value/expression.
	 * This is only valid of def elements such as variable definitions, which can be reference in expressions,
	 * and have an associated type, but are not types themselves.
	 */
	public abstract INamedElement resolveTypeForValueContext(IModuleResolver mr);
	
}