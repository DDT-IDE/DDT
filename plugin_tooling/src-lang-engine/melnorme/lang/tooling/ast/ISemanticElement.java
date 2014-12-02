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
package melnorme.lang.tooling.ast;

import java.nio.file.Path;

import melnorme.lang.tooling.engine.scoping.CommonScopeLookup;

public interface ISemanticElement {
	
	/** 
	 * @return true if this is a pre-defined/native language element. 
	 * (example: primitives such as int, void, or native types like arrays, pointer types).
	 * This is a special case for which the elements do not have a well defined containing module path. 
	 */
	public boolean isLanguageIntrinsic();
	
	/**
	 * @returnt the path of the module file from where this element was parsed or created.
	 * This is important because it is used to find which semantic context to use for the semantic element.
	 * Non-null in most cases, but it can be null.
	 */
	public Path getModulePath();
	
	
	public void evaluateForScopeLookup(CommonScopeLookup lookup, boolean importsOnly, boolean isSequentialLookup);
	
}