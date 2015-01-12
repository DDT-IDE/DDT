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
package melnorme.lang.tooling.engine.resolver;

import melnorme.lang.tooling.engine.ElementResolution;
import melnorme.lang.tooling.symbols.INamedElement;

public class ReferenceResult extends ElementResolution<INamedElement> {
	
	public ReferenceResult(INamedElement result) {
		super(result);
	}
	
	public INamedElement getSingleResult() {
		return result;
	}
	
}