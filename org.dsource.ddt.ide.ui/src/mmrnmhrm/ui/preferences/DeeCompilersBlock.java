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


import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;

import java.lang.reflect.InvocationTargetException;

import melnorme.lang.ide.core.LangCore;
import melnorme.lang.ide.dltk.ui.interpreters.CompilersBlock;
import mmrnmhrm.core.DeeCoreMessages;
import mmrnmhrm.core.engine_client.SearchAndAddCompilersTask;
import mmrnmhrm.dltk.ui.interpreters.AddScriptInterpreterDialog;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.IInterpreterInstallType;
import org.eclipse.dltk.launching.InterpreterStandin;
import org.eclipse.dltk.launching.ScriptRuntime;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class DeeCompilersBlock extends CompilersBlock {
	
	@Override
	protected AddScriptInterpreterDialog createInterpreterDialogDo(IInterpreterInstall standin) {
		IInterpreterInstallType[] deeInstallTypes = ScriptRuntime.getInterpreterInstallTypes(LangCore.NATURE_ID);
		assertTrue(deeInstallTypes.length > 0);
		return new AddDeeCompilerDialog(this, getShell(), deeInstallTypes, standin);
	}
	
	@Override
	protected void searchButtonPressed() {
		SearchTaskRunnable str = new SearchTaskRunnable();
		
		try {
			ProgressMonitorDialog progress = new ProgressMonitorDialog(getShell());
			progress.run(true, true, str);
		} catch (InvocationTargetException e) {
			throw melnorme.utilbox.core.ExceptionAdapter.unchecked(e.getCause());
		} catch (InterruptedException e) {
			return; // cancelled
		}
		
		addElements(str.searchTask.getFoundInstalls());
	}
	
	protected final class SearchTaskRunnable implements IRunnableWithProgress {
		protected volatile SearchAndAddCompilersTask searchTask;
		
		@Override
		public void run(IProgressMonitor monitor) {
			monitor.beginTask(DeeCoreMessages.SearchAndAddCompilersOnPath_JobName, IProgressMonitor.UNKNOWN);
			searchTask = new SearchAndAddCompilersTask(monitor) {
				@Override
				protected IInterpreterInstall getExistingInstall(IInterpreterInstallType installType,
						IFileHandle location) {
					for (InterpreterStandin install : getElements()) {
						if(install.getInterpreterInstallType().getId().equals(installType.getId()) &&
							install.getInstallLocation().equals(location)) {
							return install;
						}
					}
					return null;
				}
			};
			searchTask.searchForCompilers();
		}
	}
	
}