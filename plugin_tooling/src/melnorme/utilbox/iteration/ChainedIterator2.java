/*******************************************************************************
 * Copyright (c) 2013, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/

package melnorme.utilbox.iteration;


/**
 * An iterator composed of two sub-iterators, one iterated after the other. 
 * Also supports creating a copy of the current state of iteration.
 */
public class ChainedIterator2<T> extends AbstractIterator<T> implements ICopyableIterator<T> {
	
	public static <U> ICopyableIterator<U> create(ICopyableIterator<U> firstIter, ICopyableIterator<U> secondIter) {
		return new ChainedIterator2<U>(firstIter, secondIter);
	}
	
	protected ICopyableIterator<T> firstIter;
	protected final ICopyableIterator<T> secondIter;
	
	public ChainedIterator2(ICopyableIterator<T> firstIter, ICopyableIterator<T> secondIter) {
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
	public ICopyableIterator<T> copyState() {
		return new ChainedIterator2<T>(firstIter.copyState(), secondIter.copyState());
	}
	
	@Override
	public ICopyableIterator<T> optimizedSelf() {
		if(firstIter.hasNext()) {
			firstIter = firstIter.optimizedSelf();
			return this;
		} else {
			return secondIter.optimizedSelf();
		}
	}
	
}