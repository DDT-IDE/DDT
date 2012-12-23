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

package mmrnmhrm.core.parser;


import mmrnmhrm.core.DeeCore;

import org.dsource.ddt.ide.core.DeeNature;
import org.dsource.ddt.ide.core.model.DeeModuleDeclaration;
import org.dsource.ddt.ide.core.model.engine.DeeSourceElementProvider;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.core.AbstractSourceElementParser;

public class DeeSourceElementParser extends AbstractSourceElementParser {
	
	public DeeSourceElementParser() {
	}
	
	@Override
	protected String getNatureId() {
		return DeeNature.NATURE_ID;
	}
	
	@Override
	public void parseSourceModule(IModuleSource module) {
		final ModuleDeclaration moduleDeclaration = parse(module);
		if (moduleDeclaration != null) {
			DeeModuleDeclaration deeModuleDecl = (DeeModuleDeclaration) moduleDeclaration;
			DeeSourceElementProvider provider = new DeeSourceElementProvider(getRequestor());
//			final SourceElementRequestVisitor requestor = createVisitor();
			
			try {
				provider.provide(deeModuleDecl);
//				moduleDeclaration.traverse(requestor);
			} catch (RuntimeException e) {
				DeeCore.log(e);
				throw e;
			}
		}
	}
	
}
