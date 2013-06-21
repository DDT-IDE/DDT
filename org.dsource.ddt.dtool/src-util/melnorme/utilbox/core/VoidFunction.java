package melnorme.utilbox.core;


public interface VoidFunction<T> extends Function<T, Void> {
	
	@Override
	Void evaluate(T obj);
	
}