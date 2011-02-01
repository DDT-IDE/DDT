package melnorme.util.ui.fields;

import melnorme.swtutil.GridComposite;

import org.eclipse.jdt.internal.ui.util.PixelConverter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;

/** 
 * A field with an item list purely for selection. 
 * Each selection item can be divided into categories. 
 */
public class ItemSelectionListField extends DialogField {

	/** A named category holding a list of SelectionListItem. */
	public static class SelectionListCategory {
		public String name;
		public SelectionListItem[] items;
		
		public SelectionListCategory(String name, SelectionListItem[] items) {
			this.name = name;
			this.items = items;
		}
		
		@Override
		public String toString() {
			return name;
		}
	}

	/** A selection element for the item selection list . */
	public static class SelectionListItem {
		public String name;
		
		public SelectionListItem(String name) {
			this.name = name;
		}
		
		@Override
		public String toString() {
			return name;
		}

	}
	
	/** Content provider for the selection items and categories. */
	private class SelectionListContentProvider implements ITreeContentProvider {

		@Override
		public Object[] getElements(Object inputElement) {
			return catRoot;
		}
		
		@Override
		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof SelectionListCategory) {
				SelectionListCategory elem = (SelectionListCategory) parentElement;
				return elem.items;
			}
			return null;
		}

		@Override
		public Object getParent(Object element) {
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			return element instanceof SelectionListCategory;
		}


		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}
	
	/** Tree viewer for the item selection */
	private TreeViewer fTreeViewer;
	/** the tree Control */
	private Tree fTreeControl;
	
	/** Root array holding all coloring categories */
	protected SelectionListCategory[] catRoot;
	
	
	public ItemSelectionListField(SelectionListCategory[] catRoot) {
		this.catRoot = catRoot;

	}
	
	/** Perfoms a fill without specifying layout data. */
	public Composite doFillWithoutGrid(Composite parent) {
		Composite content = createContent(parent);

		return content;
	}
	
	@Override
	public Control[] doFillIntoGrid(Composite parent, int nColumns) {
		assertEnoughColumns(nColumns);
		
		Composite content = createContent(parent);
		GridData gd = (GridData) content.getLayoutData();
		gd.horizontalSpan = nColumns;
		return new Control[] { content };
	}

	private Composite createContent(Composite parent) {
		Composite content = new GridComposite(parent, 1, false);
		
		Label label= getLabelControl(content);
		label.setLayoutData(gridDataForLabel(1));
		
		TreeViewer treeViewer = getSelectionTreeViewer(content);
		
		PixelConverter converter= new PixelConverter(parent);
		GridData gd = new GridData();
		gd.widthHint = converter.convertWidthInCharsToPixels(25);
		gd.heightHint = converter.convertHeightInCharsToPixels(10);
		treeViewer.getControl().setLayoutData(gd);
		return content;
	}

	/** Gets the field's TreeViewer. */
	public TreeViewer getSelectionTreeViewer(Composite parent) {
		if(fTreeViewer == null) {
			assertCompositeNotNull(parent);

			fTreeViewer = new TreeViewer(parent, SWT.SINGLE | SWT.BORDER);
		    fTreeViewer.setLabelProvider(new LabelProvider());
		    fTreeViewer.setContentProvider(new SelectionListContentProvider());
		    fTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
				@Override
				public void selectionChanged(SelectionChangedEvent event) {
					dialogFieldChanged();
				}
		    });
		    fTreeViewer.setInput(this); // input doesn't matter
		    fTreeViewer.expandAll();
		} 
		return fTreeViewer;
		
	}
	
	/** Gets the field's Tree Control. */
	public Tree getTreeControl(Composite parent) {
		return (Tree) getSelectionTreeViewer(parent).getControl();
	}
	
	@Override
	protected void updateEnableState() {
		super.updateEnableState();
		if (isOkToUse(fTreeControl)) {
			fTreeControl.setEnabled(isEnabled());
		}	
	}
	
	/** Return the selected non-category element, or null if none is selected */
	public SelectionListItem getSelectedItem() {
		IStructuredSelection selection; 
		selection = (IStructuredSelection) fTreeViewer.getSelection();
		Object element = selection.getFirstElement();
		if (element instanceof SelectionListItem)
			return (SelectionListItem) element;
		return null;
	}
}
