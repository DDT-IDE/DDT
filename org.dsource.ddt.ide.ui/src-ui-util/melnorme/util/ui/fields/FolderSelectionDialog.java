/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package melnorme.util.ui.fields;


import org.eclipse.core.resources.IContainer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.NewFolderDialog;
import org.eclipse.ui.views.navigator.ResourceComparator;

/**
 * Dialog for the selection of an existing folder. 
 */
public class FolderSelectionDialog extends ElementTreeSelectionDialog implements ISelectionChangedListener {

	private Button fNewFolderButton;
	private IContainer fSelectedContainer;

	public FolderSelectionDialog(Shell parent, ILabelProvider labelProvider, ITreeContentProvider contentProvider) {
		super(parent, labelProvider, contentProvider);
		setComparator(new ResourceComparator(ResourceComparator.NAME));
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite result= (Composite)super.createDialogArea(parent);
		
		getTreeViewer().addSelectionChangedListener(this);
		
		Button button = new Button(result, SWT.PUSH);
		button.setText(NewWizardMessages.FolderSelectionDialog_button); 
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				newFolderButtonPressed();
			}
		});
		button.setFont(parent.getFont());
		fNewFolderButton= button;
		
		applyDialogFont(result);

		//PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, IActualHelpContextIds.BP_SELECT_DEFAULT_OUTPUT_FOLDER_DIALOG);
		
		return result;
	}

	private void updateNewFolderButtonState() {
		IStructuredSelection selection= (IStructuredSelection) getTreeViewer().getSelection();
		fSelectedContainer= null;
		if (selection.size() == 1) {
			Object first= selection.getFirstElement();
			if (first instanceof IContainer) {
				fSelectedContainer= (IContainer) first;
			}
		}
		fNewFolderButton.setEnabled(fSelectedContainer != null);
	}	
	
	protected void newFolderButtonPressed() {
		NewFolderDialog dialog= new NewFolderDialog(getShell(), fSelectedContainer) {
			@Override
			protected Control createContents(Composite parent) {
				//PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, IActualHelpContextIds.BP_CREATE_NEW_FOLDER);
				return super.createContents(parent);
			}
		};
		if (dialog.open() == Window.OK) {
			TreeViewer treeViewer= getTreeViewer();
			treeViewer.refresh(fSelectedContainer);
			Object createdFolder= dialog.getResult()[0];
			treeViewer.reveal(createdFolder);
			treeViewer.setSelection(new StructuredSelection(createdFolder));
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		updateNewFolderButtonState();
	}
}
