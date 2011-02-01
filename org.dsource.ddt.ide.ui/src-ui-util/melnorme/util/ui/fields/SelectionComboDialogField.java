package melnorme.util.ui.fields;

import org.eclipse.jdt.internal.ui.wizards.dialogfields.ComboDialogField;
import org.eclipse.swt.SWT;

public class SelectionComboDialogField<ELEM> extends ComboDialogField {
	
	ELEM[] fObjectItems;

	public SelectionComboDialogField() {
		super(SWT.READ_ONLY);
	}

	
	public void setObjectItems(ELEM[] items) {
		fObjectItems = items;
		String[] strArray = new String[items.length];
		for(int i = 0; i < items.length; i++ ) {
			strArray[i] = items[i].toString();
		}
		super.setItems(strArray);
		selectItem(0);
	}
	
	public ELEM getSelectedObject() {
		return fObjectItems[super.getSelectionIndex()];
	}
}
