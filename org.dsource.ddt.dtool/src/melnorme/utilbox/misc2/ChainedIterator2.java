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

package melnorme.utilbox.misc2;


/**
 * An iterator composed of two sub-iterators, one iterated after the other. 
 * Also supports creating a copy of the current state of iteration.
 */
public class ChainedIterator2<T> extends ChainedIterator<T> implements ICopyableIterator<T> {
	
	public static <U> ICopyableIterator<U> create(
		ICopyableIterator<? extends U> firstIter,
		ICopyableIterator<? extends U> secondIter) {
		return new ChainedIterator2<U>(firstIter, secondIter);
	}
	
	public ChainedIterator2(
		ICopyableIterator<? extends T> firstIter, ICopyableIterator<? extends T> secondIter) {
		super(firstIter, secondIter);
	}
	
	@Override
	public ICopyableIterator<T> copyState() {
		ICopyableIterator<? extends T> firstIter = (ICopyableIterator<? extends T>) this.firstIter;
		ICopyableIterator<? extends T> secondIter = (ICopyableIterator<? extends T>) this.secondIter;
		return new ChainedIterator2<T>(firstIter.copyState(), secondIter.copyState());
	}
	
}