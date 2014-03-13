/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation (JDT)
 *     DLTK team ? - DLTK modifications 
 *     Bruno Medeiros - modifications, removed script elements
 *******************************************************************************/
package melnorme.lang.ide.dltk.ui.interpreters;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertFail;

import java.util.List;

import melnorme.lang.ide.ui.FieldMessages;
import melnorme.lang.ide.ui.fields.TableListEditingField;
import melnorme.util.swt.jface.TypedTableLabelProvider;

import org.eclipse.dltk.core.environment.EnvironmentManager;
import org.eclipse.dltk.internal.debug.ui.interpreters.AddScriptInterpreterDialog;
import org.eclipse.dltk.internal.debug.ui.interpreters.IAddInterpreterDialogRequestor;
import org.eclipse.dltk.internal.debug.ui.interpreters.IScriptInterpreterDialog;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.InterpreterStandin;
import org.eclipse.dltk.launching.ScriptRuntime;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableColumn;

/**
 * A composite that displays installed InterpreterEnvironment's in a table.
 * InterpreterEnvironments can be added, removed, edited, and searched for.
 * <p>
 * This block implements ISelectionProvider - it sends selection change events
 * when the checked InterpreterEnvironment in the table changes, or when the
 * "use default" button check state changes.
 * </p>
 * @
 */
public abstract class CompilersBlock extends TableListEditingField<InterpreterStandin> 
	implements IAddInterpreterDialogRequestor {
	
	protected Button buttonSearch;
	
	@Override
	public Composite createControl(Composite ancestor) {
		super.createControl(ancestor);
		
		// by default, sort by name
		sortByName();

		return componentParent;
	}
	
	@Override
	protected void createButtons(Composite buttons) {
		super.createButtons(buttons);
		
		buttonCopy.dispose();
		
		Label separator = new Label(buttons, SWT.NONE);
		separator.setVisible(false);
		separator.setLayoutData(
			GridDataFactory.swtDefaults().align(SWT.FILL, SWT.BEGINNING).hint(SWT.DEFAULT, 4).create());
		
		buttonSearch = createPushButton(buttons, FieldMessages.ButtonLabel_Search);
		buttonSearch.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event evt) {
				searchButtonPressed();
			}
		});
	}
	
	@Override
	protected void createTableColumns() {
		createColumn(0, InterpretersMessages.InstalledInterpreters_LocationColumnLabel);
		createColumn(1, InterpretersMessages.InstalledInterpreters_TypeColumnLabel);
		createColumn(2, InterpretersMessages.InstalledInterpreters_IdColumnLabel);
		
		restoreColumnLayoutData(0, 150);
		restoreColumnLayoutData(1, 50);
		restoreColumnLayoutData(2, 150);
	}
	
	@Override
	protected void restoreColumnLayoutData(int columnIndex, TableColumn column, int width) {
		if(columnIndex == 1) {
			column.setResizable(false);
			tableLayout.setColumnData(column, new ColumnPixelData(40, false));
		} else {
			tableLayout.setColumnData(column, new ColumnWeightData(10, width, true));
		}
	}
	
	@Override
	protected InterpreterLabelProvider createLabelProvider() {
		return new InterpreterLabelProvider();
	}
	
	protected static class InterpreterLabelProvider extends TypedTableLabelProvider<InterpreterStandin> {
		
		@Override
		public String getColumnText0(InterpreterStandin element, int columnIndex) {
			switch (columnIndex) {
			case 0:
				return element.getRawInstallLocation().toOSString();
			case 1:
				return element.getInterpreterInstallType().getName();
			case 2:
				return element.getName();
			}
			return "";
		}
		
		@Override
		public Image getColumnImage0(InterpreterStandin element, int columnIndex) {
			if (columnIndex == 0) {
			}
			return null;
		}
	}
	
	@Override
	protected void sortByColumn(int columnIndex) {
		sortingColumn = columnIndex;
		switch (columnIndex) {
		case 0:
			sortByLocation();
			break;
		case 1:
			sortByType();
			break;
		case 2:
			sortByName();
			break;
		}
	}
	
	protected void sortByType() {
		elementsTableViewer.setComparator(new ViewerComparator() {
			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				if ((e1 instanceof IInterpreterInstall) && (e2 instanceof IInterpreterInstall)) {
					IInterpreterInstall left = (IInterpreterInstall) e1;
					IInterpreterInstall right = (IInterpreterInstall) e2;
					String leftType = left.getInterpreterInstallType().getName();
					String rightType = right.getInterpreterInstallType().getName();
					int res = leftType.compareToIgnoreCase(rightType);
					if (res != 0) {
						return res;
					}
					return left.getName().compareToIgnoreCase(right.getName());
				}
				return super.compare(viewer, e1, e2);
			}
		});
	}
	
	protected void sortByName() {
		elementsTableViewer.setComparator(new ViewerComparator() {
			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				if ((e1 instanceof IInterpreterInstall) && (e2 instanceof IInterpreterInstall)) {
					IInterpreterInstall left = (IInterpreterInstall) e1;
					IInterpreterInstall right = (IInterpreterInstall) e2;
					return left.getName().compareToIgnoreCase(right.getName());
				}
				return super.compare(viewer, e1, e2);
			}
		});
	}
	
	protected void sortByLocation() {
		elementsTableViewer.setComparator(new ViewerComparator() {
			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				if ((e1 instanceof IInterpreterInstall) && (e2 instanceof IInterpreterInstall)) {
					IInterpreterInstall left = (IInterpreterInstall) e1;
					IInterpreterInstall right = (IInterpreterInstall) e2;
					String leftInstallLocation = left.getInstallLocation().toString();
					String rightInstallLocation = right.getInstallLocation().toString();
					return leftInstallLocation.compareToIgnoreCase(rightInstallLocation);
				}
				return super.compare(viewer, e1, e2);
			}
		});
	}
	
	@Override
	protected void updateButtonEnablement() {
		super.updateButtonEnablement();
		
		buttonRemove.setEnabled(true);
		for(IInterpreterInstall install : elementsTableViewer.getSelection()) {
			if (isContributed(install)) {
				buttonRemove.setEnabled(false);
				return;
			}			
		}
	}
	
	protected static boolean isContributed(IInterpreterInstall install) {
		return ScriptRuntime.isContributedInterpreterInstall(install.getId());
	}
	
	/* ----------------- button actions ----------------- */
	
	@Override
	protected void addElementButtonPressed() {
		IScriptInterpreterDialog dialog = createInterpreterDialog(null);
		if (dialog == null)
			return;
		dialog.setTitle(InterpretersMessages.InstalledInterpreters_AddIntepreterDialog_Title);
		if (!dialog.execute()) {
			return;
		}
	}
	
	protected IScriptInterpreterDialog createInterpreterDialog(IInterpreterInstall standin) {
		AddScriptInterpreterDialog dialog = createInterpreterDialogDo(standin);
		dialog.setEnvironment(EnvironmentManager.getLocalEnvironment());
		return dialog;
	}
	
	protected abstract AddScriptInterpreterDialog createInterpreterDialogDo(IInterpreterInstall standin);
	
	@Override
	public boolean isDuplicateName(String name, IInterpreterInstall editedInterpreter) {
		for (InterpreterStandin install : getElements()) {
			if (install.getName().equals(name) && install != editedInterpreter) {
				return true;
			}
		}
		return false;
	}
	
	
	@Override
	public void interpreterAdded(IInterpreterInstall Interpreter) {
		if(Interpreter instanceof InterpreterStandin) {
			interpreterAddedFromDialog((InterpreterStandin) Interpreter);
		}
	}
	
	public void interpreterAddedFromDialog(InterpreterStandin install) {
		addElement(install);
		
		List<InterpreterStandin> installs = getElements();
		if (installs.size() == 1) {
			// pick a default Interpreter automatically
			InterpreterStandin element = installs.get(0);
			setCheckedElement(element);
		}
	}
	
	@Override
	protected void editElementButtonPressed() {
		IInterpreterInstall install = getFirstSelectedElement();
		if (install == null) {
			return;
		}
		
		IScriptInterpreterDialog dialog = createInterpreterDialog(install);
		if (dialog == null)
			return;
		dialog.setTitle(InterpretersMessages.InstalledInterpreters_EditIntepreterDialog_Title);
		if (!dialog.execute()) {
			return;
		}
		elementsTableViewer.refresh(install);
	}
	
	@Override
	protected void copyElementButtonPressed() {
		throw assertFail();
	}
	
	protected abstract void searchButtonPressed();
	
	// Add elements from search: TODO cleanup the #equals semantics
	protected void addElements(List<InterpreterStandin> elementsToAdd) {
		boolean wasEmpty = getElements().isEmpty();
		
		for (InterpreterStandin element : elementsToAdd) {
			if(!isDuplicateName(element.getName(), null)) {
				addElement(element);
			}
		}
		if(wasEmpty && getElements().size() > 0) {
			// set first element as default
			setCheckedElement(getElements().get(0));
		}
	}
	
}