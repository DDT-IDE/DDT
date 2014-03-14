/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package mmrnmhrm.dltk.ui.interpreters;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import melnorme.lang.ide.dltk.ui.interpreters.InterpretersMessages;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.internal.debug.ui.interpreters.LibraryContentProvider;
import org.eclipse.dltk.internal.debug.ui.interpreters.LibraryStandin;
import org.eclipse.dltk.launching.EnvironmentVariable;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.IInterpreterInstallType;
import org.eclipse.dltk.launching.LibraryLocation;
import org.eclipse.dltk.launching.ScriptRuntime;
import org.eclipse.dltk.ui.dialogs.TimeTriggeredProgressMonitorDialog;
import org.eclipse.dltk.ui.environment.IEnvironmentUI;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

// Copied from DLTK version 5.0.0, TODO: refactor

/**
 * Control used to edit the libraries associated with a Interpreter install
 */
public abstract class AbstractInterpreterLibraryBlock implements
		SelectionListener, ISelectionChangedListener {

	/**
	 * Attribute name for the last path used to open a file/directory chooser
	 * dialog.
	 */
	protected static final String LAST_PATH_SETTING = "LAST_PATH_SETTING"; //$NON-NLS-1$

	/**
	 * the prefix for dialog setting pertaining to this block
	 */
	protected static final String DIALOG_SETTINGS_PREFIX = "AbstractInterpreterLibraryBlock"; //$NON-NLS-1$

	protected boolean fInCallback = false;
	protected IInterpreterInstall fInterpreterInstall;
	protected IInterpreterInstallType fInterpreterInstallType;
	protected IFileHandle fHome;

	// widgets
	protected LibraryContentProvider fLibraryContentProvider;
	protected TreeViewer fLibraryViewer;
	private Button fUpButton;
	private Button fDownButton;
	private Button fRemoveButton;
	private Button fAddButton;
	protected Button fDefaultButton;
	protected Button fRediscoverButton;
	private Button fEnabledButton;

	protected AddScriptInterpreterDialog fDialog;

	protected AbstractInterpreterLibraryBlock(AddScriptInterpreterDialog dialog) {
		this.fDialog = dialog;
	}

	/**
	 * Creates and returns the source lookup control.
	 * 
	 * @param parent
	 *            the parent widget of this control
	 */
	public Control createControl(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		GridLayout topLayout = new GridLayout();
		topLayout.numColumns = 2;
		topLayout.marginHeight = 0;
		topLayout.marginWidth = 0;
		comp.setLayout(topLayout);
		GridData gd = new GridData(GridData.FILL_BOTH);
		comp.setLayoutData(gd);
		createControlsIn(comp);
		return comp;
	}

	/**
	 * @since 2.0
	 */
	public void createControlsIn(Composite comp) {
		Composite comp2 = new Composite(comp, SWT.NONE);
		GridLayout topLayout = new GridLayout();
		topLayout.numColumns = 1;
		topLayout.marginHeight = 0;
		topLayout.marginWidth = 0;
		comp2.setLayout(topLayout);
		GridData gd = new GridData(GridData.FILL_BOTH);
		comp2.setLayoutData(gd);
		fLibraryViewer = createViewer(comp2);
		gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 6;
		fLibraryViewer.getControl().setLayoutData(gd);
		fLibraryContentProvider = createLibraryContentProvider();
		fLibraryViewer.setContentProvider(fLibraryContentProvider);
		fLibraryViewer.setLabelProvider(getLabelProvider());
		fLibraryViewer.setInput(this);
		fLibraryViewer.addSelectionChangedListener(this);

		if (isEnableButtonSupported()) {
			fEnabledButton = new Button(comp2, SWT.CHECK);
			fEnabledButton
					.setText(InterpretersMessages.AbstractInterpreterLibraryBlock_setPathVisibleToDltk);
			fEnabledButton.addSelectionListener(this);
			this.fLibraryViewer
					.addDoubleClickListener(new IDoubleClickListener() {
						@Override
						public void doubleClick(DoubleClickEvent event) {
							if (fLibraryContentProvider
									.canEnable((IStructuredSelection) fLibraryViewer
											.getSelection())) {
								fLibraryContentProvider.changeEnabled();
								updateButtons();
							}
						}
					});
		}

		Composite pathButtonComp = new Composite(comp, SWT.NONE);
		GridLayout pathButtonLayout = new GridLayout();
		pathButtonLayout.marginHeight = 0;
		pathButtonLayout.marginWidth = 0;
		pathButtonComp.setLayout(pathButtonLayout);
		gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING
				| GridData.HORIZONTAL_ALIGN_FILL);
		pathButtonComp.setLayoutData(gd);

		fAddButton = createPushButton(pathButtonComp,
				InterpretersMessages.InterpreterLibraryBlock_7);
		fAddButton.addSelectionListener(this);

		fRemoveButton = createPushButton(pathButtonComp,
				InterpretersMessages.InterpreterLibraryBlock_6);
		fRemoveButton.addSelectionListener(this);

		fUpButton = createPushButton(pathButtonComp,
				InterpretersMessages.InterpreterLibraryBlock_4);
		fUpButton.addSelectionListener(this);

		fDownButton = createPushButton(pathButtonComp,
				InterpretersMessages.InterpreterLibraryBlock_5);
		fDownButton.addSelectionListener(this);

		fDefaultButton = createPushButton(pathButtonComp,
				InterpretersMessages.InterpreterLibraryBlock_9);
		fDefaultButton.addSelectionListener(this);
		if (this.fDialog.isRediscoverSupported()) {
			fRediscoverButton = createPushButton(
					pathButtonComp,
					InterpretersMessages.AbstractInterpreterLibraryBlock_rediscover);
			fRediscoverButton.addSelectionListener(this);
		}
	}

	protected boolean isEnableButtonSupported() {
		return false;
	}

	protected LibraryContentProvider createLibraryContentProvider() {
		return new LibraryContentProvider();
	}

	protected TreeViewer createViewer(Composite comp) {
		return new TreeViewer(comp);
	}

	/**
	 * The "default" button has been toggled
	 */
	public void restoreDefaultLibraries() {
		LibraryLocation[] libs = getLibrariesWithEnvironment(fDialog
				.getEnvironmentVariables());
		if (libs != null) {
			fLibraryContentProvider.setLibraries(libs);
			fLibraryContentProvider.initialize(getHomeDirectory(), fDialog
					.getEnvironmentVariables(), true);
		}
		update();
	}

	protected LibraryLocation[] getLibrariesWithEnvironment(
			final EnvironmentVariable[] environmentVariables) {
		final LibraryLocation[][] libs = new LibraryLocation[][] { null };
		final IFileHandle installLocation = getHomeDirectory();
		if (installLocation == null) {
			libs[0] = new LibraryLocation[0];
		} else {
			ProgressMonitorDialog dialog = new TimeTriggeredProgressMonitorDialog(
					null, 1000);
			try {
				dialog.run(true, true, new IRunnableWithProgress() {

					@Override
					public void run(IProgressMonitor monitor)
							throws InvocationTargetException,
							InterruptedException {
						libs[0] = getInterpreterInstallType()
								.getDefaultLibraryLocations(installLocation,
										environmentVariables, monitor);
					}

				});
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return libs[0];
	}

	/**
	 * Creates and returns a button
	 * 
	 * @param parent
	 *            parent widget
	 * @param label
	 *            label
	 * @return Button
	 */
	protected Button createPushButton(Composite parent, String label) {
		Button button = new Button(parent, SWT.PUSH);
		button.setFont(parent.getFont());
		button.setText(label);
		setButtonLayoutData(button);
		return button;
	}

	/**
	 * Create some empty space
	 */
	protected void createVerticalSpacer(Composite comp, int colSpan) {
		Label label = new Label(comp, SWT.NONE);
		GridData gd = new GridData();
		gd.horizontalSpan = colSpan;
		label.setLayoutData(gd);
	}

	/**
	 * Sets the home directory of the Interpreter Install the user has chosen
	 */
	public void setHomeDirectory(IFileHandle file) {
		fHome = file;
	}

	/**
	 * Returns the home directory
	 */
	protected IFileHandle getHomeDirectory() {
		return fHome;
	}

	/**
	 * Updates buttons and status based on current libraries
	 */
	public void update() {
		updateButtons();
		IStatus status = Status.OK_STATUS;
		if (fLibraryContentProvider.getLibraries().length == 0) { // &&
			// !isDefaultSystemLibrary())
			// {
			// status = new Status(
			// IStatus.ERROR,
			// DLTKDebugUIPlugin.getUniqueIdentifier(),
			// IDLTKDebugUIConstants.INTERNAL_ERROR,
			// InterpretersMessages.
			// InterpreterLibraryBlock_Libraries_cannot_be_empty__1,
			// null);
		}
		LibraryStandin[] standins = fLibraryContentProvider.getStandins();
		for (int i = 0; i < standins.length; i++) {
			IStatus st = standins[i].validate();
			if (!st.isOK()) {
				status = st;
				break;
			}
		}
		updateDialogStatus(status);
	}

	/**
	 * Saves settings in the given working copy
	 */
	public void performApply(IInterpreterInstall install) {
		if (isDefaultLocations()) {
			install.setLibraryLocations(null);
		} else {
			LibraryLocation[] libs = fLibraryContentProvider.getLibraries();
			install.setLibraryLocations(libs);
		}
	}

	/**
	 * @since 2.0
	 */
	public LibraryLocation[] getLibraryLocations() {
		if (isDefaultLocations()) {
			return null;
		} else {
			LibraryLocation[] libs = fLibraryContentProvider.getLibraries();
			return libs;
		}
	}

	/**
	 * Determines if the present setup is the default location s for this
	 * InterpreterEnvironment
	 * 
	 * @return true if the current set of locations are the defaults, false
	 *         otherwise
	 */
	protected boolean isDefaultLocations() {
		LibraryLocation[] libraryLocations = fLibraryContentProvider
				.getLibraries();
		IInterpreterInstall install = getInterpreterInstall();

		if (install == null || libraryLocations == null) {
			return true;
		}
		IFileHandle installLocation = install.getInstallLocation();
		if (installLocation != null) {
			LibraryLocation[] def = getInterpreterInstallType()
					.getDefaultLibraryLocations(installLocation,
							install.getEnvironmentVariables(), null);
			if (def.length == libraryLocations.length) {
				for (int i = 0; i < def.length; i++) {
					if (!def[i].equals(libraryLocations[i])) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the Interpreter install associated with this library block.
	 * 
	 * @return Interpreter install
	 */
	protected IInterpreterInstall getInterpreterInstall() {
		return fInterpreterInstall;
	}

	/**
	 * Returns the Interpreter install type associated with this library block.
	 * 
	 * @return Interpreter install
	 */
	protected IInterpreterInstallType getInterpreterInstallType() {
		return fInterpreterInstallType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt
	 * .events.SelectionEvent)
	 */
	@Override
	public void widgetSelected(SelectionEvent e) {
		Object source = e.getSource();
		if (source == fUpButton) {
			fLibraryContentProvider.up((IStructuredSelection) fLibraryViewer
					.getSelection());
		} else if (source == fDownButton) {
			fLibraryContentProvider.down((IStructuredSelection) fLibraryViewer
					.getSelection());
		} else if (source == fRemoveButton) {
			fLibraryContentProvider
					.remove((IStructuredSelection) fLibraryViewer
							.getSelection());
			this.fDialog.updateValidateInterpreterLocation();
		} else if (source == fAddButton) {
			add((IStructuredSelection) fLibraryViewer.getSelection());
		} else if (source == fDefaultButton) {
			restoreDefaultLibraries();
			this.fDialog.updateValidateInterpreterLocation();
		} else if (source == fRediscoverButton) {
			this.reDiscover(this.fDialog.getEnvironmentVariables(), null);
		} else if (source == fEnabledButton) {
			this.fLibraryContentProvider.changeEnabled();
		}
		update();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse
	 * .swt.events.SelectionEvent)
	 */
	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
	}

	private void add(IStructuredSelection selection) {
		LibraryLocation libs = add();
		if (libs == null)
			return;
		fLibraryContentProvider.add(new LibraryLocation[] { libs }, selection);
		// We need to reinitialize.
		fLibraryContentProvider.initialize(this.getHomeDirectory(), fDialog
				.getEnvironmentVariables(), false);
		update();
		this.fDialog.updateValidateInterpreterLocation();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(
	 * org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		updateButtons();
	}

	/**
	 * Refresh the enable/disable state for the buttons.
	 */
	private void updateButtons() {
		IStructuredSelection selection = (IStructuredSelection) fLibraryViewer
				.getSelection();
		fRemoveButton.setEnabled(fLibraryContentProvider.canRemove(selection));
		boolean enableUp = true, enableDown = true;
		Object[] libraries = fLibraryContentProvider.getElements(null);
		if (selection.isEmpty() || libraries.length == 0) {
			enableUp = false;
			enableDown = false;
			if (isEnableButtonSupported()) {
				fEnabledButton.setSelection(false);
				fEnabledButton.setEnabled(false);
			}
		} else {
			Object first = libraries[0];
			Object last = libraries[libraries.length - 1];
			if (isEnableButtonSupported()) {
				fEnabledButton.setEnabled(fLibraryContentProvider
						.canEnable(selection));
			}
			for (Iterator iter = selection.iterator(); iter.hasNext();) {
				Object element = iter.next();
				Object lib;
				lib = element;
				if (isEnableButtonSupported() && selection.size() == 1) {
					fEnabledButton.setSelection(fLibraryContentProvider
							.isEnabled(lib));
				}
				if (lib == first) {
					enableUp = false;
				}
				if (lib == last) {
					enableDown = false;
				}
			}
		}
		fUpButton.setEnabled(enableUp
				&& fLibraryContentProvider.canUp(selection));
		fDownButton.setEnabled(enableDown
				&& fLibraryContentProvider.canUp(selection));
	}

	/**
	 * Initializes this control based on the settings in the given Interpreter
	 * install and type.
	 * 
	 * @param Interpreter
	 *            Interpreter or <code>null</code> if none
	 * @param type
	 *            type of Interpreter install
	 */

	public void initializeFrom(IInterpreterInstall Interpreter,
			IInterpreterInstallType type) {
		fInterpreterInstall = Interpreter;
		fInterpreterInstallType = type;
		if (Interpreter != null) {
			setHomeDirectory(Interpreter.getInstallLocation());
			// fLibraryContentProvider.setLibraries(ScriptRuntime
			// .getLibraryLocations(getInterpreterInstall(), null));
			final LibraryLocation[][] libs = new LibraryLocation[][] { null };
			ProgressMonitorDialog dialog = new TimeTriggeredProgressMonitorDialog(
					null, 3000);
			try {
				dialog.run(true, true, new IRunnableWithProgress() {

					@Override
					public void run(IProgressMonitor monitor)
							throws InvocationTargetException,
							InterruptedException {
						libs[0] = ScriptRuntime.getLibraryLocations(
								getInterpreterInstall(), monitor);
					}

				});
			} catch (InvocationTargetException e) {
				if (DLTKCore.DEBUG) {
					e.printStackTrace();
				}
			} catch (InterruptedException e) {
				if (DLTKCore.DEBUG) {
					e.printStackTrace();
				}
			}
			fLibraryContentProvider.setLibraries(libs[0]);

			// Set All possibly libraries here
			fLibraryContentProvider.initialize(getHomeDirectory(), fDialog
					.getEnvironmentVariables(), false);
		}
		update();
	}

	protected abstract IBaseLabelProvider getLabelProvider();

	protected void updateDialogStatus(IStatus status) {
		fDialog.setSystemLibraryStatus(status);
		fDialog.updateStatusLine();
	}

	protected void setButtonLayoutData(Button button) {
		fDialog.setButtonLayoutData(button);
	}

	/**
	 * @deprecated
	 */
	protected IDialogSettings getDialogSettions() {
		return null;
	}

	protected LibraryLocation add() {
		IEnvironment environment = fDialog.getEnvironment();
		IEnvironmentUI ui = (IEnvironmentUI) environment
				.getAdapter(IEnvironmentUI.class);
		String res = ui.selectFolder(fLibraryViewer.getControl().getShell());
		if (res == null) {
			return null;
		}

		IPath path = EnvironmentPathUtils.getFullPath(environment,
				new Path(res));
		LibraryLocation lib = new LibraryLocation(path.makeAbsolute());
		return lib;
	}

	/**
	 * Rediscover using following technicue:
	 * 
	 * 1) Keep all user added entries.
	 * 
	 * 2) Remove all default entries.
	 * 
	 * 3) Rediscover
	 * 
	 * 4) Add all new entries to list.
	 * 
	 * @param environmentVariables
	 * @param oldVars
	 */
	public void reDiscover(EnvironmentVariable[] environmentVariables,
			EnvironmentVariable[] oldVars) {
		// if (oldVars == null) {
		// if (this.fInterpreterInstall != null) {
		// oldVars = this.fInterpreterInstall.getEnvironmentVariables();
		// }
		// // if( oldVars != null && oldVars.length == 0 ) {
		// // restoreDefaultLibraries();
		// // return;
		// // }
		// }
		// if (oldVars == null) {
		// if (this.fInterpreterInstall == null) {
		// restoreDefaultLibraries();
		// }
		// return;
		// }
		// // Skip re discover if variables are same.
		// if (equals(environmentVariables, oldVars)) {
		// return;
		// }
		// LibraryLocation[] currentLibraries = this.fLibraryContentProvider
		// .getLibraries();
		//
		// LibraryLocation[] oldLibs = getLibrariesWithEnvironment(oldVars);
		// LibraryLocation[] newLibs =
		// getLibrariesWithEnvironment(environmentVariables);
		// // If current are equal to old, we could easy set new libs.
		// if (equals(currentLibraries, oldLibs)) {
		// if (newLibs != null)
		// fLibraryContentProvider.setLibraries(newLibs);
		// } else { // We need to build delta.
		// Set delta = new HashSet();
		// delta.addAll(Arrays.asList(currentLibraries));
		// delta.removeAll(Arrays.asList(oldLibs));
		//
		// List newList = new ArrayList();
		// newList.addAll(Arrays.asList(newLibs));
		// for (Iterator iterator = delta.iterator(); iterator.hasNext();) {
		// LibraryLocation lib = (LibraryLocation) iterator.next();
		// if (!newList.contains(lib)) {
		// newList.add(lib);
		// }
		// }
		//
		// LibraryLocation[] aNew = (LibraryLocation[]) newList
		// .toArray(new LibraryLocation[delta.size()]);
		// fLibraryContentProvider.setLibraries(aNew);
		// }
		fLibraryContentProvider.initialize(getHomeDirectory(), fDialog
				.getEnvironmentVariables(), false);
		update();
	}

	private boolean equals(EnvironmentVariable[] a, EnvironmentVariable[] b) {
		Map vars = new HashMap();
		if (a.length != b.length) {
			return false;
		}
		for (int i = 0; i < a.length; i++) {
			vars.put(a[i].getName(), a[i]);
		}
		for (int i = 0; i < b.length; i++) {
			EnvironmentVariable v = (EnvironmentVariable) vars.get(b[i]
					.getName());
			if (v == null) {
				return false;
			}
			if (!v.getValue().equals(b[i].getValue())) {
				return false;
			}
		}
		return true;
	}

	private boolean equals(LibraryLocation[] a, LibraryLocation[] b) {
		Set libs = new HashSet();
		if (a.length != b.length) {
			return false;
		}
		for (int i = 0; i < a.length; i++) {
			libs.add(a[i]);
		}
		for (int i = 0; i < b.length; i++) {
			if (!libs.contains(b[i])) {
				return false;
			}
		}
		return true;
	}
}
