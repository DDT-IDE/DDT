package melnorme.utilbox.misc2;

import java.util.Iterator;

/**
 * Interface for an iterator that allows to create a copy of current state of iteration. 
 */
public interface ICopyableIterator<T> extends Iterator<T> {
	
	public ICopyableIterator<T> copyState();
	
	/** Returns a version of this object which has been optimized according to current state. 
	 * Can either return the receiver instance itself, or another object with equal state. In this later case, 
	 * the receiver instance is not valid anymore. */
	public ICopyableIterator<T> optimizedSelf();
	
}