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
package dtool.ast.definitions;

import melnorme.lang.tooling.ast.IASTNode;
import melnorme.lang.tooling.ast.INamedElementNode;
import melnorme.lang.tooling.context.ISemanticContext;
import dtool.ast.expressions.Resolvable;


public interface ITemplateParameter extends IASTNode {
	
	INamedElementNode createTemplateArgument(Resolvable argument, ISemanticContext tplRefContext);
	
}