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
package melnorme.lang.tooling.ast_actual;

import melnorme.lang.tooling.symbols.INamedElement;
import descent.core.ddoc.Ddoc;
import dtool.ast.definitions.DefUnit;
import dtool.ast.definitions.EArcheType;

public interface ILangNamedElement extends INamedElement {
	
	/** Gets the archetype (the kind) of this DefElement. */
	EArcheType getArcheType();
	
	/** @return the DefUnit this def element represents. In most cases this is the same as the receiver, 
	 * but this method allows proxy {@link ILangNamedElement} classes to resolve to their proxied {@link DefUnit}. 
	 * It may still return null since the underlying defunit may not exist at all (implicitly defined named elements).
	 */
	DefUnit resolveDefUnit();
	
	/** Resolve the underlying element and return its DDoc. See {@link #resolveDefUnit()}.
	 * Can be null. */
	Ddoc resolveDDoc();
	
}