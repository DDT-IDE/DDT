package melnorme.utilbox.misc2;

import java.util.Iterator;

/**
 * Interface for an iterator that allows to create a copy of current state of iteration. 
 */
public interface ICopyableIterator<T> extends Iterator<T> {
	
	public ICopyableIterator<T> copyState();
	
}