/*******************************************************************************
 * Copyright (c) 2011, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.lang.tooling.engine.resolver;

import melnorme.lang.tooling.ast.ILanguageElement;
import melnorme.lang.tooling.context.ISemanticContext;

/** 
 * A node that is a reference (or a value implicitly referring) to a named element.
 */
public interface IResolvable extends ILanguageElement {
	
	@Override
	public ResolvableSemantics getSemantics(ISemanticContext parentContext);
	
	public String toStringAsCode();
	
}