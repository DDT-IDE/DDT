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
package dtool.util;

// XXX: Deprecate this in favor of parent class?
public class ArrayView<E> extends melnorme.utilbox.collections.ArrayView<E> {
	
	public static <T> ArrayView<T> create(T[] arr){
		return new ArrayView<T>(arr);
	}
	
	public ArrayView(E[] array) {
		super(array);
	}
	
}