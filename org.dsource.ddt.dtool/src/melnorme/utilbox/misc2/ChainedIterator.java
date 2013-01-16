/*******************************************************************************
 * Copyright (c) 2009, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.utilbox.misc2;

import java.util.Iterator;

/**
 * An iterator composed of two sub-iterators, one iterated after the other. 
 */
public class ChainedIterator<T> implements Iterator<T> {
	
	protected final Iterator<? extends T> firstIter;
	protected final Iterator<? extends T> secondIter;
	
	public static <U> Iterator<? extends U> create(
		Iterator<? extends U> firstIter,
		Iterator<? extends U> secondIter) {
		return new ChainedIterator<U>(firstIter, secondIter);
	}
	
	public ChainedIterator(Iterator<? extends T> firstIter, Iterator<? extends T> secondIter) {
		this.firstIter = firstIter;
		this.secondIter = secondIter;
	}
	
	@Override
	public boolean hasNext() {
		return firstIter.hasNext() || secondIter.hasNext();
		
	}
	
	@Override
	public T next() {
		if(firstIter.hasNext())
			return firstIter.next();
		return secondIter.next();
		
	}
	
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
}