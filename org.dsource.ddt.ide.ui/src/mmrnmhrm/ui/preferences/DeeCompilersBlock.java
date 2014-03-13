/*******************************************************************************
 * Copyright (c) 2008, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.ui.preferences;


import java.lang.reflect.InvocationTargetException;
import java.util.List;

import mmrnmhrm.core.DeeCoreMessages;
import mmrnmhrm.core.projectmodel.SearchAndAddCompilersOnPathTask;
import mmrnmhrm.dltk.internal.debug.ui.interpreters.CompilersBlock;

import org.dsource.ddt.ide.core.DeeNature;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.internal.debug.ui.interpreters.AddScriptInterpreterDialog;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.IInterpreterInstallType;
import org.eclipse.dltk.launching.InterpreterStandin;
import org.eclipse.dltk.launching.ScriptRuntime;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;

// TODO: rewrite InterpretersBlock, remove host and "interpreters" names
public class DeeCompilersBlock extends CompilersBlock {
	
	@Override
	protected String getCurrentNature() {
		return DeeNature.NATURE_ID;
	}
	
	@Override
	protected AddScriptInterpreterDialog createInterpreterDialog(IInterpreterInstall standin) {
		IInterpreterInstallType[] deeInstallTypes = ScriptRuntime.getInterpreterInstallTypes(getCurrentNature());
		DialogAddDeeCompiler dialog = new DialogAddDeeCompiler(this, getShell(), deeInstallTypes, standin);
		return dialog;
	}
	
	@Override
	protected void search() {
		SearchTaskRunnable str = new SearchTaskRunnable();
		
		try {
			ProgressMonitorDialog progress = new ProgressMonitorDialog(getShell());
			progress.run(true, true, str);
		} catch (InvocationTargetException e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e.getCause());
		} catch (InterruptedException e) {
			return; // cancelled
		}
		
		List<InterpreterStandin> foundInstalls = str.searchTask.getFoundInstalls();
		for (InterpreterStandin interpreterStandin : foundInstalls) {
			if(!isDuplicateName(interpreterStandin.getName(), null)) {
				interpreterAdded(interpreterStandin);
			}
		}
	}
	
	protected final class SearchTaskRunnable implements IRunnableWithProgress {
		protected volatile SearchAndAddCompilersOnPathTask searchTask;
		
		@Override
		public void run(IProgressMonitor monitor) {
			monitor.beginTask(DeeCoreMessages.SearchAndAddCompilersOnPath_JobName, IProgressMonitor.UNKNOWN);
			searchTask = new SearchAndAddCompilersOnPathTask(monitor);
			searchTask.searchForCompilers();
		}
	}
	
}