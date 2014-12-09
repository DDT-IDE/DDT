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
package melnorme.lang.tooling.engine;

import java.util.Map;

import melnorme.lang.tooling.ast.ILanguageElement;

/**
 * A class responsible for doing semantic analysis.
 * Each instance is bound to a specific {@link ILanguageElement}, except for NULL_NODE_SEMANTICS.
 * This class uses the {@link #hashCode()} and {@link #equals()} of Object, such that each instance of 
 * this class can be seperately inserted in a {@link Map}. 
 */
public interface IElementSemantics {
	
	@Override
	public boolean equals(Object obj);
	
	@Override
	public int hashCode();
	
}