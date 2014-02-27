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

package mmrnmhrm.core.model_elements;


import mmrnmhrm.core.DeeCore;
import mmrnmhrm.core.parser.DeeModuleDeclaration;

import org.dsource.ddt.ide.core.DeeNature;
import org.eclipse.dltk.ast.parser.IModuleDeclaration;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.core.AbstractSourceElementParser;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.SourceParserUtil;

public class DeeSourceElementParser extends AbstractSourceElementParser {
	
	public DeeSourceElementParser() {
	}
	
	@Override
	protected String getNatureId() {
		return DeeNature.NATURE_ID;
	}
	
	protected DeeModuleDeclaration parse2(IModuleSource module) {
		if (module instanceof ISourceModule) {
			ISourceModule sourceModule = (ISourceModule) module;
			return (DeeModuleDeclaration) SourceParserUtil.parse(sourceModule, getProblemReporter());
		} else {
			// parse directly without cache
			final IModuleDeclaration result = SourceParserUtil.parse(module, getNatureId(), getProblemReporter());
			return (DeeModuleDeclaration) result;
		}
	}
	
	@Override
	public void parseSourceModule(IModuleSource module) {
		final IModuleDeclaration moduleDeclaration = parse2(module);
		if (moduleDeclaration != null) {
			DeeModuleDeclaration deeModuleDecl = (DeeModuleDeclaration) moduleDeclaration;
			DeeSourceElementProvider provider = new DeeSourceElementProvider(getRequestor());
//			final SourceElementRequestVisitor requestor = createVisitor();
			
			try {
				provider.provide(deeModuleDecl);
//				moduleDeclaration.traverse(requestor);
			} catch (RuntimeException e) {
				DeeCore.logError(e);
				throw e;
			}
		}
	}
	
}
