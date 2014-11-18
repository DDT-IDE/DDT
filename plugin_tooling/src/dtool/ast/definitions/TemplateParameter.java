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

import melnorme.lang.tooling.ast.INamedElementNode;
import dtool.ast.expressions.Resolvable;

/**
 * TODO clean up template parameter semantics a bit
 */
public abstract class TemplateParameter extends DefUnit {
	
	public TemplateParameter(ProtoDefSymbol defId) {
		super(defId);
	}
	
	public abstract INamedElementNode createTemplateArgument(Resolvable argument);
	
}