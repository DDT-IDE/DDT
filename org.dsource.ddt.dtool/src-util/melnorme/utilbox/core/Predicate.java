package melnorme.utilbox.core;

/**
 * A delegate method that given an argument of type T returns true or false 
 */
public interface Predicate<T> {

	boolean evaluate(T obj);
	
}
