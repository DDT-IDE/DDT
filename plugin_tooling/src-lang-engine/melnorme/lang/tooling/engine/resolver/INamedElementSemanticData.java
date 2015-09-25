/*******************************************************************************
 * Copyright (c) 2015 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.lang.tooling.engine.resolver;

import melnorme.lang.tooling.engine.IElementSemanticData;
import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;
import melnorme.lang.tooling.symbols.INamedElement;


public interface INamedElementSemanticData extends IElementSemanticData {
	
	public ConcreteElementResult resolveConcreteElement();
	
	/** 
	 * Return the type of this defElement, when it is referenced as a value/expression.
	 * This is only valid of def elements such as variable definitions, which can be referenced 
	 * in expressions and have an associated type, but are not types themselves.
	 * @return non-null.
	 */
	public INamedElement getTypeForValueContext();
	
	public void resolveSearchInMembersScope(CommonScopeLookup search);
	
}