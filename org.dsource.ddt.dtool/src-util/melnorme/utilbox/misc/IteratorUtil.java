/*******************************************************************************
 * Copyright (c) 2007 DSource.org and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial implementation
 *******************************************************************************/
package melnorme.utilbox.misc;


import java.util.Collections;
import java.util.Iterator;

public class IteratorUtil { 
	
	public static final Iterator<?> EMPTY_ITERATOR = Collections.EMPTY_LIST.iterator();
	
	@SuppressWarnings("unchecked")
	public static <T> Iterator<T> getEMPTY_ITERATOR() {
		return (Iterator<T>) EMPTY_ITERATOR;
	}
	
	/** Recasts the type parameter of given iterator to a more specific type.
	 * Safe to do if the returned iterator is used in a read only way with regards to the underlying collection.
	 * @return the recasted iterator. */
	@SuppressWarnings("unchecked")
	public static <T> Iterator<T> recast(Iterator<? extends T> iterator) {
		return ((Iterator<T>) iterator);
	}
	
	public static <T> Iterator<T> singletonIterator(T elem) {
		return Collections.singletonList(elem).iterator();
	}
	
}
