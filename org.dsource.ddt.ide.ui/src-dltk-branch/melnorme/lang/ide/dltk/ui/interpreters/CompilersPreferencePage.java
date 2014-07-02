/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation (JDT)
 *     DLTK team ? - DLTK modifications 
 *******************************************************************************/
package melnorme.lang.ide.dltk.ui.interpreters;

import static melnorme.utilbox.core.CoreUtil.listFrom;

import java.util.ArrayList;
import java.util.List;

import melnorme.lang.ide.ui.LangUIPlugin;
import melnorme.util.swt.components.IFieldValueListener;
import melnorme.utilbox.misc.ArrayUtil;
import mmrnmhrm.core.workspace.WorkspaceModelManager;

import org.eclipse.dltk.core.environment.EnvironmentManager;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.debug.ui.interpreters.InterpretersUpdater;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.IInterpreterInstallType;
import org.eclipse.dltk.launching.InterpreterStandin;
import org.eclipse.dltk.launching.ScriptRuntime;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public abstract class CompilersPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
	
	public static final String COMPILERS_PREF_PAGE = "CompilersPrefPage";
	
	// InterpreterEnvironment Block
	private CompilersBlock fInterpretersBlock;
	
	public CompilersPreferencePage() {
		super();
		
		setTitle(InterpretersMessages.InterpretersPreferencePage_1);
		setDescription(InterpretersMessages.InterpretersPreferencePage_2);
	}
	
	@Override
	public void init(IWorkbench workbench) {
	}
	
	protected abstract String getNature();
	
	@Override
	protected Control createContents(Composite ancestor) {
		noDefaultAndApplyButton();
		
		fInterpretersBlock = createInterpretersBlock();
		Composite composite = fInterpretersBlock.createComponent(ancestor);
		composite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		
		fInterpretersBlock.restoreTableSettings(getSettingsSection());
		
		fInterpretersBlock.addValueChangedListener(new IFieldValueListener() {
			@Override
			public void fieldValueChanged() {
				InterpreterStandin checkedElement = fInterpretersBlock.getCheckedElement();
				
				setErrorMessage(null);
				if(fInterpretersBlock.getElements().size() > 0 && checkedElement == null) {
					setErrorMessage(InterpretersMessages.InterpreterPreferencePage_pleaseSetDefaultInterpreter);
					
				} else if(fInterpretersBlock.getElements().size() == 0) {
					setErrorMessage(InterpretersMessages.InterpreterPreferencePage_addInterpreter);
				}
			}
		});
		
		initWithWorkspaceInterpreters();
		
		applyDialogFont(ancestor);
		return composite;
	}
	
	public abstract CompilersBlock createInterpretersBlock();
	
	protected IDialogSettings getSettingsSection() {
		return DialogSettings.getOrCreateSection(getDialogSettings(), COMPILERS_PREF_PAGE);
	}
	
	protected IDialogSettings getDialogSettings() {
		return LangUIPlugin.getInstance().getDialogSettings();
	}
	
	protected void initWithWorkspaceInterpreters() {
		// The plugin might be loaded just when this pref page is activaded, 
		// so make sure we wait for the compilers search job to finish.
		try {
			WorkspaceModelManager.getDefault().getCompilersSearchJob().join();
		} catch (InterruptedException e) {
		}
		
		// fill with interpreters
		List<InterpreterStandin> standins = new ArrayList<InterpreterStandin>();
		IInterpreterInstallType[] installTypes = ScriptRuntime.getInterpreterInstallTypes(getNature());
		for (IInterpreterInstallType type : installTypes) {
			IInterpreterInstall[] installs = type.getInterpreterInstalls();
			
			for (IInterpreterInstall install : listFrom(installs)) {
				standins.add(new InterpreterStandin(install));
			}
		}
		fInterpretersBlock.setElements(standins);
		
		initDefaultInterpreter();
	}
	
	protected void initDefaultInterpreter() {
		IEnvironment[] environments = EnvironmentManager.getEnvironments();
		for (IEnvironment environment : environments) {
			IInterpreterInstall realDefault = ScriptRuntime.getDefaultInterpreterInstall(getNature(), environment);
			
			if (realDefault != null) {
				for (InterpreterStandin standinInterpreter : fInterpretersBlock.getElements()) {
					if (standinInterpreter.equals(realDefault)) {
						fInterpretersBlock.setCheckedElement(standinInterpreter);
						break;
					}
				}
			}
		}
	}
	
	@Override
	public boolean performOk() {
		final boolean[] canceled = new boolean[] { false };
		BusyIndicator.showWhile(null, new Runnable() {
			@Override
			public void run() {
				IInterpreterInstall[] defaultInterpreters = 
						ArrayUtil.singletonArray(fInterpretersBlock.getCheckedElement(), IInterpreterInstall.class);
				InterpreterStandin[] interpreters = fInterpretersBlock.getElementsArray(InterpreterStandin.class);
				
				InterpretersUpdater updater = new InterpretersUpdater();
				if (!updater.updateInterpreterSettings(getNature(), 
					interpreters, defaultInterpreters)) {
					canceled[0] = true;
				}
			}
		});
		
		if (canceled[0]) {
			return false;
		}
		
		// save column widths
		fInterpretersBlock.saveTableSettings(getSettingsSection());
		
		return super.performOk();
	}
	
}