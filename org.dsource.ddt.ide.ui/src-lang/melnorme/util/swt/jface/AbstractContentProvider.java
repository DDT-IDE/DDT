package melnorme.util.swt.jface;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;


/**
 * Abstract content provider
 */
public abstract class AbstractContentProvider implements ITreeContentProvider {
	
	protected Viewer viewer;
	protected Object input;
	
	@Override
	public void dispose() {
	}
	
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = viewer;
		this.input = newInput;
	}
	
	@Override
	public Object[] getElements(Object inputElement) {
		assertTrue(input == inputElement);
		return getChildren(inputElement);
	}
	
}