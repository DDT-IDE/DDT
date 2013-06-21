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

	@SuppressWarnings("unchecked")
	public static <T> Iterator<T> recast(Iterator<? extends T> iter) {
		return ((Iterator<T>) iter);
	}

	public static <T> Iterator<T> singletonIterator(T elem) {
		return Collections.singletonList(elem).iterator();
	}

}
