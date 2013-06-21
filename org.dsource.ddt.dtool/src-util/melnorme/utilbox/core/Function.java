package melnorme.utilbox.core;

public interface Function<T, R> {
	
	R evaluate(T obj);
	
}