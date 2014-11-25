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



public class ResolutionEntry<E extends ElementResolution<?>> {
	
	protected E result;
	
	public E getResult() {
		return result;
	}
	
	public void putResult(E newResult) {
		this.result = newResult;
	}
	
}