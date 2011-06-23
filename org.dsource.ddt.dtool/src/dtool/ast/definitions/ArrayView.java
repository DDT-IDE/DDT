package dtool.ast.definitions;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertNotNull;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

import dtool.ArrayUtilExt;
import dtool.ast.IASTNeoNode;

public class ArrayView<T> implements Iterable<T>, RandomAccess {
	
	
	@SuppressWarnings("unchecked")
	public static <T extends IASTNeoNode> ArrayView<T> createFrom(T[] arr){
		return (ArrayView<T>) create(arr == null ? ArrayUtilExt.EMPTY_ARRAY : arr);
	}
	
	public static <T> ArrayView<T> create(T[] arr){
		return new ArrayView<T>(arr);
	}
	
	protected T[] array;
	
	public ArrayView(T[] array) {
		assertNotNull(array);
		this.array = array;
	}
	
	public final int size() {
		return array.length;
	}
	
	public final boolean isEmpty() {
		return array.length == 0;
	}
	
	public final T get(int index) {
		return array[index];
	}
	
	@Override
	public final Iterator<T> iterator() {
		return new ArrayIterator();
	}
	
	public final class ArrayIterator implements Iterator<T> {
		int index = 0;
		
		public ArrayIterator() {
		}
		
		@Override
		public boolean hasNext() {
			return index < array.length;
		}
		
		@Override
		public T next() throws NoSuchElementException {
			if (!hasNext())
				throw new NoSuchElementException();
			return array[index++];
		}
		
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}