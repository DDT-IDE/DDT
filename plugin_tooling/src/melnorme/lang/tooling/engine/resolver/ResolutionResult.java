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

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.util.List;

import melnorme.lang.tooling.symbols.INamedElement;
import melnorme.utilbox.collections.ArrayList2;

public class ResolutionResult {
	
	protected final List<INamedElement> results;
	
	public ResolutionResult(INamedElement... results) {
		this.results = new ArrayList2<>(results);
	}
	
	public INamedElement getSingleResult() {
		assertTrue(results.size() <= 1);
		if(results.isEmpty()) {
			return null;
		}
		return results.get(0);
	}
	
}