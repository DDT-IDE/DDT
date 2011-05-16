/*******************************************************************************
 * Copyright (c) 2011, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package org.dsource.ddt.ide.core.model;

import org.eclipse.dltk.ast.parser.IModuleDeclaration;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.SourceParserUtil;

public class DeeParserUtil {
	
	/** Parses the module and returns an AST. Returns null if given module is not the DDT nature. 
	 * This operation uses caching for the created AST. */
	public static DeeModuleDeclaration getASTFromModule(final ISourceModule module) {
		IModuleDeclaration moduleDeclaration = SourceParserUtil.parse(module, null);
		
		if (moduleDeclaration instanceof DeeModuleDeclaration) {
			DeeModuleDeclaration deeModuleDecl = (DeeModuleDeclaration) moduleDeclaration;
			return deeModuleDecl;
		}
		return null;
	}
	
}
