/*******************************************************************************
 * Copyright (c) 2010, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package org.dsource.ddt.lang.text;


/**
 * Symbols for the heuristic scanner.
 */
public interface ILangHeuristicSymbols {
	
	int TOKEN_EOF = -1;
	int TOKEN_INVALID = -2;
	int TOKEN_OUTSIDE = -3; // Token for whole partitions that we skip over
	
}
