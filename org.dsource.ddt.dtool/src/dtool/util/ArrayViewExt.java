package dtool.util;


public class ArrayViewExt<E> extends ArrayView<E> {
	
	public static <T> ArrayViewExt<T> create(T[] arr){
		return new ArrayViewExt<T>(arr);
	}
	
	public ArrayViewExt(E[] array) {
		super(array);
	}
	
	/** Accesses and returns the *internal* array backing this {@link ArrayViewExt}, for which clients must not modify!
	 * Warning: this method is not safe, it can break if the array with which this {@link ArrayViewExt} was created
	 * is not of component type E, and yet it is assigned to a E[] */
	public final E[] getInternalArray() {
		return array;
	}
	
}