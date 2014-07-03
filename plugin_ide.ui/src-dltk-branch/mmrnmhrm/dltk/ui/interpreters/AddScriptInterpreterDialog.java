/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
  * Contributors:
 *     IBM Corporation - initial API and implementation (JDT)
 *     DLTK team ? - DLTK modifications 
 *     Bruno Medeiros - modifications
 *******************************************************************************/
package mmrnmhrm.dltk.ui.interpreters;

import java.lang.reflect.InvocationTargetException;

import melnorme.lang.ide.dltk.ui.interpreters.InterpretersMessages;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.compiler.util.Util;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.core.internal.environment.LazyFileHandle;
import org.eclipse.dltk.debug.ui.DLTKDebugUIPlugin;
import org.eclipse.dltk.internal.debug.ui.interpreters.AbstractInterpreterEnvironmentVariablesBlock;
import org.eclipse.dltk.internal.debug.ui.interpreters.IAddInterpreterDialogRequestor;
import org.eclipse.dltk.internal.debug.ui.interpreters.IScriptInterpreterDialog;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.ComboDialogField;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.IStringButtonAdapter;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.StringButtonDialogField;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.StringDialogField;
import org.eclipse.dltk.launching.EnvironmentVariable;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.IInterpreterInstallType;
import org.eclipse.dltk.launching.InterpreterStandin;
import org.eclipse.dltk.launching.LibraryLocation;
import org.eclipse.dltk.ui.dialogs.StatusInfo;
import org.eclipse.dltk.ui.dialogs.TimeTriggeredProgressMonitorDialog;
import org.eclipse.dltk.ui.environment.IEnvironmentUI;
import org.eclipse.dltk.utils.PlatformFileUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

//Copied from DLTK version 5.0.0, TODO: refactor
//TODO: rewrite AddScriptInterpreterDialog

public abstract class AddScriptInterpreterDialog extends StatusDialog implements
		IScriptInterpreterDialog {

	/**
	 * @since 2.0
	 */
	protected final IAddInterpreterDialogRequestor fRequestor;

	private IInterpreterInstallType[] fInterpreterTypes;

	/**
	 * @since 2.0
	 */
	protected IInterpreterInstallType fSelectedInterpreterType;

	private ComboDialogField fInterpreterTypeCombo;

	/**
	 * @since 2.0
	 */
	protected final IInterpreterInstall fEditedInterpreter;

	/**
	 * @since 2.0
	 */
	protected AbstractInterpreterLibraryBlock fLibraryBlock;
	/**
	 * @since 2.0
	 */
	protected AbstractInterpreterEnvironmentVariablesBlock fEnvironmentVariablesBlock;

	private StringButtonDialogField fInterpreterPath;

	private StringDialogField fInterpreterName;

	private StringDialogField fInterpreterArgs;

	private IStatus[] fStati;
	private int fPrevIndex = -1;

	private IEnvironment environment;

	protected boolean useInterpreterArgs() {
		return true;
	}

	public AddScriptInterpreterDialog(IAddInterpreterDialogRequestor requestor,
			Shell shell, IInterpreterInstallType[] interpreterInstallTypes,
			IInterpreterInstall editedInterpreter) {
		super(shell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		fRequestor = requestor;
		fStati = new IStatus[5];
		for (int i = 0; i < fStati.length; i++) {
			fStati[i] = new StatusInfo();
		}

		fInterpreterTypes = interpreterInstallTypes;
		fSelectedInterpreterType = editedInterpreter != null ? editedInterpreter
				.getInterpreterInstallType()
				: interpreterInstallTypes[0];

		fEditedInterpreter = editedInterpreter;
	}

	/**
	 * @see Windows#configureShell
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		// PlatformUI.getWorkbench().getHelpSystem().setHelp(newShell,
		// IScriptDebugHelpContextIds.EDIT_InterpreterEnvironment_DIALOG);
	}

	protected void createDialogFields() {
		fInterpreterTypeCombo = new ComboDialogField(SWT.READ_ONLY);
		fInterpreterTypeCombo
				.setLabelText(InterpretersMessages.addInterpreterDialog_InterpreterEnvironmentType);
		fInterpreterTypeCombo.setItems(getInterpreterTypeNames());

		fInterpreterName = new StringDialogField();
		fInterpreterName
				.setLabelText(InterpretersMessages.addInterpreterDialog_InterpreterEnvironmentName);

		fInterpreterPath = new StringButtonDialogField(
				new IStringButtonAdapter() {
					@Override
					public void changeControlPressed(DialogField field) {
						browseForInstallation();
					}
				});
		fInterpreterPath
				.setLabelText(InterpretersMessages.addInterpreterDialog_InterpreterExecutableName);
		fInterpreterPath
				.setButtonLabel(InterpretersMessages.addInterpreterDialog_browse1);

		if (this.useInterpreterArgs()) {
			fInterpreterArgs = new StringDialogField();
			fInterpreterArgs
					.setLabelText(InterpretersMessages.AddInterpreterDialog_iArgs);
		}
	}

	protected void createFieldListeners() {

		fInterpreterTypeCombo
				.setDialogFieldListener(new IDialogFieldListener() {
					@Override
					public void dialogFieldChanged(DialogField field) {
						updateInterpreterType();
					}
				});

		fInterpreterName.setDialogFieldListener(new IDialogFieldListener() {
			@Override
			public void dialogFieldChanged(DialogField field) {
				setInterpreterNameStatus(validateInterpreterName());
				updateStatusLine();
			}
		});

		fInterpreterPath.setDialogFieldListener(new IDialogFieldListener() {
			@Override
			public void dialogFieldChanged(DialogField field) {
				updateValidateInterpreterLocation();
				fLibraryBlock.restoreDefaultLibraries();
				updateStatusLine();
			}
		});

	}

	protected String getInterpreterName() {
		return fInterpreterName.getText();
	}

	protected void setInterpreterName(String value) {
		fInterpreterName.setText(value);
	}

	// protected File getInstallLocation() {
	// return new File(fInterpreterPath.getText()).getAbsoluteFile();
	// }

	protected abstract AbstractInterpreterLibraryBlock createLibraryBlock(
			AddScriptInterpreterDialog dialog);

	/**
	 * @param dialog
	 * @return
	 */
	protected AbstractInterpreterEnvironmentVariablesBlock createEnvironmentVariablesBlock() {
		return createEnvironmentVariablesBlock(this);
	}

	/**
	 * @param dialog
	 * @return
	 * @deprecated createEnvironmentVariablesBlock() without parameters should
	 *             be overridden when needed
	 */
	protected AbstractInterpreterEnvironmentVariablesBlock createEnvironmentVariablesBlock(
			AddScriptInterpreterDialog dialog) {
		return null;
	}

	@Override
	protected Control createDialogArea(Composite ancestor) {
		final Composite parent = (Composite) super.createDialogArea(ancestor);
		final int numColumns = 3;
		((GridLayout) parent.getLayout()).numColumns = numColumns;

		createSimpleFields(parent, numColumns);

		final Composite blockComposite = new Composite(parent, SWT.NONE);
		final GridData blockCompositeLayoutData = new GridData(SWT.FILL,
				SWT.FILL, true, true);
		blockCompositeLayoutData.horizontalSpan = numColumns;
		blockComposite.setLayoutData(blockCompositeLayoutData);
		final GridLayout blockCompositeLayout = new GridLayout(2, false);
		blockCompositeLayout.marginHeight = 0;
		blockCompositeLayout.marginWidth = 0;
		blockComposite.setLayout(blockCompositeLayout);

		createDialogBlocks(blockComposite, 2);

		initializeFields(fEditedInterpreter);
		createFieldListeners();
		applyDialogFont(parent);
		return parent;
	}

	/**
	 * @since 2.0
	 */
	protected void createSimpleFields(Composite parent, final int numColumns) {
		createDialogFields();

		fInterpreterTypeCombo.doFillIntoGrid(parent, numColumns);
		((GridData) fInterpreterTypeCombo.getComboControl(null).getLayoutData()).widthHint = convertWidthInCharsToPixels(50);

		fInterpreterPath.doFillIntoGrid(parent, numColumns);
		final GridData interpreterPathGridData = (GridData) fInterpreterPath
				.getTextControl(null).getLayoutData();
		interpreterPathGridData.grabExcessHorizontalSpace = true;
		interpreterPathGridData.widthHint = convertWidthInCharsToPixels(50);

		fInterpreterName.doFillIntoGrid(parent, numColumns);

		if (this.useInterpreterArgs()) {
			fInterpreterArgs.doFillIntoGrid(parent, numColumns);
			((GridData) fInterpreterArgs.getTextControl(null).getLayoutData()).widthHint = convertWidthInCharsToPixels(50);
		}
	}

	/**
	 * @since 2.0
	 */
	protected void createDialogBlocks(Composite parent, int numColumns) {
		Composite libraryBlockParent = createLibraryBlockParent(parent,
				numColumns);

		fLibraryBlock = createLibraryBlock(this);
		fLibraryBlock.createControlsIn(libraryBlockParent);

		fEnvironmentVariablesBlock = createEnvironmentVariablesBlock();
		if (fEnvironmentVariablesBlock != null) {
			Composite envParent = createEnvironmentVariablesBlockParent(parent,
					numColumns);

			fEnvironmentVariablesBlock.createControlsIn(envParent);
		}
	}

	/**
	 * @since 2.0
	 */
	protected Composite createEnvironmentVariablesBlockParent(Composite parent,
			int numColumns) {
		Label l = new Label(parent, SWT.NONE);
		l
				.setText(InterpretersMessages.addInterpreterDialog_interpreterEnvironmentVariables);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = numColumns;
		l.setLayoutData(gd);
		return parent;
	}

	/**
	 * @since 2.0
	 */
	protected Composite createLibraryBlockParent(Composite parent,
			int numColumns) {
		Label l = new Label(parent, SWT.NONE);
		l
				.setText(InterpretersMessages.addInterpreterDialog_Interpreter_system_libraries__1);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = numColumns;
		l.setLayoutData(gd);
		return parent;
	}

	private void updateInterpreterType() {
		int selIndex = fInterpreterTypeCombo.getSelectionIndex();
		if (selIndex == fPrevIndex) {
			return;
		}
		fPrevIndex = selIndex;
		if (selIndex >= 0 && selIndex < fInterpreterTypes.length) {
			fSelectedInterpreterType = fInterpreterTypes[selIndex];
		}
		updateValidateInterpreterLocation();
		fLibraryBlock.initializeFrom(fEditedInterpreter,
				fSelectedInterpreterType);
		if (fEnvironmentVariablesBlock != null) {
			fEnvironmentVariablesBlock.initializeFrom(fEditedInterpreter,
					fSelectedInterpreterType);
		}
		updateStatusLine();
	}

	/**
	 * @since 2.0
	 */
	protected void updateValidateInterpreterLocation() {
		setInterpreterLocationStatus(validateInterpreterLocation());
	}

	@Override
	public void create() {
		super.create();
		fInterpreterPath.setFocus();
		selectInterpreterType();
	}

	private String[] getInterpreterTypeNames() {
		String[] names = new String[fInterpreterTypes.length];
		for (int i = 0; i < fInterpreterTypes.length; i++) {
			names[i] = fInterpreterTypes[i].getName();
		}
		return names;
	}

	/**
	 * @since 2.0
	 */
	private void selectInterpreterType() {
		for (int i = 0; i < fInterpreterTypes.length; i++) {
			if (fSelectedInterpreterType.getId().equals(
					fInterpreterTypes[i].getId())) {
				fInterpreterTypeCombo.selectItem(i);
				return;
			}
		}
	}

	/**
	 * @since 2.0
	 */
	protected void initializeFields(IInterpreterInstall install) {
		fInterpreterTypeCombo.setItems(getInterpreterTypeNames());
		if (install == null) {
			fInterpreterName.setText(Util.EMPTY_STRING);
			fInterpreterPath.setText(Util.EMPTY_STRING);
			fLibraryBlock.initializeFrom(null, fSelectedInterpreterType);
			if (fEnvironmentVariablesBlock != null) {
				fEnvironmentVariablesBlock.initializeFrom(null,
						fSelectedInterpreterType);
			}
			if (this.useInterpreterArgs()) {
				fInterpreterArgs.setText(Util.EMPTY_STRING);
			}
		} else {
			fInterpreterTypeCombo.setEnabled(false);
			fInterpreterName.setText(install.getName());
			fInterpreterPath.setText(install.getRawInstallLocation()
					.toOSString());
			if (fEnvironmentVariablesBlock != null) {
				fEnvironmentVariablesBlock.initializeFrom(install,
						fSelectedInterpreterType);
			}
			fLibraryBlock.initializeFrom(install, fSelectedInterpreterType);
			String InterpreterArgs = install.getInterpreterArgs();
			if (InterpreterArgs != null) {
				fInterpreterArgs.setText(InterpreterArgs);
			}
		}
		setInterpreterNameStatus(validateInterpreterName());
		updateStatusLine();
	}

	protected IInterpreterInstallType getInterpreterType() {
		return fSelectedInterpreterType;
	}

	protected IStatus validateInterpreterLocation() {
		final IStatus s;
		final IFileHandle file;
		final Path location = new Path(fInterpreterPath.getText());
		if (location.isEmpty()) {
			file = null;
			s = new StatusInfo(IStatus.INFO,
					InterpretersMessages.addInterpreterDialog_enterLocation);
		} else {
			file = PlatformFileUtils.findAbsoluteOrEclipseRelativeFile(
					getEnvironment(), location);
			if (!file.exists()) {
				s = new StatusInfo(
						IStatus.ERROR,
						InterpretersMessages.addInterpreterDialog_locationNotExists);
			} else {
				s = validateInterpreter(file);
			}
		}
		if (s != null && s.isOK()) {
			fLibraryBlock.setHomeDirectory(file);

			String name = fInterpreterName.getText();
			if ((name == null || name.trim().length() == 0) && file != null) {
				// auto-generate interpreter name
				String pName = generateInterpreterName(file);
				if (pName != null) {
					fInterpreterName.setText(pName);
				}
			}
		} else {
			fLibraryBlock.setHomeDirectory(null);
		}
		// fLibraryBlock.restoreDefaultLibraries();
		// if (fEnvironmentVariablesBlock != null) {
		// fEnvironmentVariablesBlock.restoreDefaultVariables();
		// }
		return s;
	}

	private IStatus validateInterpreter(final IFileHandle file) {
		final IStatus[] temp = new IStatus[1];
		TimeTriggeredProgressMonitorDialog progressDialog = new TimeTriggeredProgressMonitorDialog(
				this.getShell(), 200);
		try {
			progressDialog.run(false, false, new IRunnableWithProgress() {
				@Override
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					EnvironmentVariable[] environmentVariables = null;
					if (fEnvironmentVariablesBlock != null) {
						environmentVariables = fEnvironmentVariablesBlock
								.getEnvironmentVariables();
					}
					LibraryLocation[] locations = fLibraryBlock
							.getLibraryLocations();
					temp[0] = getInterpreterType().validateInstallLocation(
							file, environmentVariables, locations, monitor);
				}
			});
		} catch (InvocationTargetException e) {
			DLTKCore.error(e);
		} catch (InterruptedException e) {
			DLTKCore.error(e);
		}
		return temp[0];
	}

	/**
	 * Generates unique interpreter name based on the file selected
	 * 
	 * @param file
	 * @return generated name or <code>null</code> if it was not possible to
	 *         generate the suitable name
	 */
	protected String generateInterpreterName(IFileHandle file) {
		final String genName;
		final IPath path = new Path(file.getCanonicalPath());
		if (path.segmentCount() > 0) {
			genName = path.lastSegment();
		} else {
			genName = null;
		}
		// Add number if interpreter with such name already exists.
		String pName = genName;
		if (pName != null) {
			int index = 0;
			while (!(validateGeneratedName(pName) && !fRequestor
					.isDuplicateName(pName, fEditedInterpreter))) {
				pName = genName + "(" + String.valueOf(++index) //$NON-NLS-1$
						+ ")"; //$NON-NLS-1$
			}
		}
		return pName;
	}

	/**
	 * Validates the automatically generated interpreter name
	 * 
	 * @param name
	 * @return <code>true</code> if specified name is unique and
	 *         <code>false</code> otherwise
	 */
	protected boolean validateGeneratedName(String name) {
		for (int i = 0; i < this.fInterpreterTypes.length; i++) {
			IInterpreterInstallType type = this.fInterpreterTypes[i];
			IInterpreterInstall inst = type.findInterpreterInstallByName(name);
			if (inst != null) {
				// it is allowed to find interpreter being edited.
				return inst == fEditedInterpreter;
			}
		}
		return true;
	}

	public IEnvironment getEnvironment() {
		return this.environment;
	}

	public void setEnvironment(IEnvironment environment) {
		this.environment = environment;
	}

	private IStatus validateInterpreterName() {
		StatusInfo status = new StatusInfo();
		String name = fInterpreterName.getText();
		if (name == null || name.trim().length() == 0) {
			status.setInfo(InterpretersMessages.addInterpreterDialog_enterName);
		} else {
			if (fRequestor.isDuplicateName(name, fEditedInterpreter)
					&& (fEditedInterpreter == null || !name
							.equals(fEditedInterpreter.getName()))) {
				status
						.setError(InterpretersMessages.addInterpreterDialog_duplicateName);
			}
		}
		return status;
	}

	@Override
	public void updateStatusLine() {
		IStatus max = null;
		for (int i = 0; i < fStati.length; i++) {
			IStatus curr = fStati[i];
			if (curr.matches(IStatus.ERROR)) {
				updateStatus(curr);
				return;
			}
			if (max == null || curr.getSeverity() > max.getSeverity()) {
				max = curr;
			}
		}
		updateStatus(max);
	}

	private void browseForInstallation() {
		IEnvironment environment = getEnvironment();
		IEnvironmentUI environmentUI = (IEnvironmentUI) environment
				.getAdapter(IEnvironmentUI.class);
		if (environmentUI != null) {
			String newPath = environmentUI.selectFile(getShell(),
					IEnvironmentUI.EXECUTABLE, fInterpreterPath.getText()
							.trim());
			if (newPath != null) {
				fInterpreterPath.setText(newPath);
			}
		}
	}

	@Override
	protected void okPressed() {
		doOkPressed();
		super.okPressed();
	}

	private IInterpreterInstall lastInstall = null;

	private void doOkPressed() {
		if (fEditedInterpreter == null) {
			IInterpreterInstall install = new InterpreterStandin(
					fSelectedInterpreterType,
					createUniqueId(fSelectedInterpreterType));
			setFieldValuesToInterpreter(install);
			fRequestor.interpreterAdded(install);
			lastInstall = install;
		} else {
			setFieldValuesToInterpreter(fEditedInterpreter);
			lastInstall = fEditedInterpreter;
		}
	}

	private String createUniqueId(IInterpreterInstallType InterpreterType) {
		String id = null;
		do {
			id = String.valueOf(System.currentTimeMillis());
		} while (InterpreterType.findInterpreterInstall(id) != null);
		return id;
	}

	protected void setFieldValuesToInterpreter(IInterpreterInstall install) {
		IEnvironment selectedEnv = getEnvironment();
		install.setInstallLocation(new LazyFileHandle(selectedEnv.getId(),
				new Path(fInterpreterPath.getText())));
		install.setName(fInterpreterName.getText());

		if (this.useInterpreterArgs()) {
			String argString = fInterpreterArgs.getText().trim();

			if (argString != null && argString.length() > 0) {
				install.setInterpreterArgs(argString);
			} else {
				install.setInterpreterArgs(null);
			}
		} else {
			install.setInterpreterArgs(null);
		}

		fLibraryBlock.performApply(install);
		if (fEnvironmentVariablesBlock != null) {
			fEnvironmentVariablesBlock.performApply(install);
		}
	}

	// protected File getAbsoluteFileOrEmpty(String path) {
	// if (path == null || path.length() == 0) {
	// return new File(""); //$NON-NLS-1$
	// }
	// return new File(path).getAbsoluteFile();
	// }

	private void setInterpreterNameStatus(IStatus status) {
		fStati[1] = status;
	}

	private void setInterpreterLocationStatus(IStatus status) {
		fStati[0] = status;
	}

	/**
	 * @since 2.0
	 */
	protected IStatus getInterpreterLocationStatus() {
		return fStati[0];
	}

	protected IStatus getSystemLibraryStatus() {
		return fStati[3];
	}

	public void setSystemLibraryStatus(IStatus status) {
		fStati[3] = status;

	}

	/**
	 * Updates the status of the ok button to reflect the given status.
	 * Subclasses may override this method to update additional buttons.
	 * 
	 * @param status
	 *            the status.
	 */
	@Override
	protected void updateButtonsEnableState(IStatus status) {
		Button ok = getButton(IDialogConstants.OK_ID);
		if (ok != null && !ok.isDisposed())
			ok.setEnabled(status.getSeverity() == IStatus.OK);
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#setButtonLayoutData(org.eclipse.swt.widgets.Button)
	 */
	@Override
	public void setButtonLayoutData(Button button) {
		super.setButtonLayoutData(button);
		((GridData) button.getLayoutData()).grabExcessHorizontalSpace = true;
	}

	/**
	 * Returns the name of the section that this dialog stores its settings in
	 * 
	 * @return String
	 */
	protected String getDialogSettingsSectionName() {
		return "ADD_INTERPRETER_DIALOG_SECTION"; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#getDialogBoundsSettings()
	 */
	@Override
	protected IDialogSettings getDialogBoundsSettings() {
		IDialogSettings settings = DLTKDebugUIPlugin.getDefault()
				.getDialogSettings();
		IDialogSettings section = settings
				.getSection(getDialogSettingsSectionName());
		if (section == null) {
			section = settings.addNewSection(getDialogSettingsSectionName());
		}
		return section;
	}

	public EnvironmentVariable[] getEnvironmentVariables() {
		AbstractInterpreterEnvironmentVariablesBlock environmentVariablesBlock = this.fEnvironmentVariablesBlock;
		if (environmentVariablesBlock != null) {
			return environmentVariablesBlock.getEnvironmentVariables();
		}
		if (this.fEditedInterpreter != null) {
			return this.fEditedInterpreter.getEnvironmentVariables();
		}
		return null;
	}

	/**
	 * Re discover libraries if environment variables are changed.
	 * 
	 * @param environmentVariables
	 */
	public void updateLibraries(EnvironmentVariable[] newVars,
			EnvironmentVariable[] oldVars) {
		fLibraryBlock.reDiscover(newVars, oldVars);
	}

	protected boolean isRediscoverSupported() {
		return false;
	}

	/**
	 * This method could be used only after okPressed.
	 */
	protected IInterpreterInstall getLastInterpreterInstall() {
		return this.lastInstall;
	}

	@Override
	public boolean execute() {
		return open() == Window.OK;
	}

	/**
	 * @since 2.0
	 */
	protected String getInterpreterPath() {
		return fInterpreterPath.getText();
	}
}
