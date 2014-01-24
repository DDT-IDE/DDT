/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.lang.ui;


import melnorme.lang.ide.core.LangCore;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Shell;



public class OperationExceptionHandler {
	
	private static OperationExceptionHandler fgInstance = new OperationExceptionHandler();
	
	public static OperationExceptionHandler getDefault() {
		return fgInstance;
	}
	
	public static void handle(CoreException e, String title, String message) {
		handle(e, DLTKUIPlugin.getActiveWorkbenchShell(), title, message);
	}
	
	
	public static void handle(CoreException e, Shell parent, String title, String message) {
		getDefault().perform(e, parent, title, message);
	}
	
	public static void handle(RuntimeException e, String title, String message) {
		handle(e, DLTKUIPlugin.getActiveWorkbenchShell(), title, message);
	}
	
	public static void handle(RuntimeException e, Shell parent, String title, String message) {
		getDefault().perform(e, parent, title, message);
	}
	
	// ------------------------------------
	
	protected void perform(CoreException e, Shell shell, String title, String dialogMessage) {
		LangCore.log(e);
		ErrorDialog.openError(shell, title, dialogMessage, e.getStatus());
	}
	
	protected void perform(RuntimeException e, Shell shell, String title, String dialogMessage) {
		LangCore.log(e);
		String statusMessage = e.getMessage();
		if (e.getMessage() != null && !e.getMessage().isEmpty()) {
			statusMessage = e.getMessage();
		} else {
			statusMessage = e.getClass().getSimpleName();
		}
		Status status = LangCore.createErrorStatus(statusMessage, e);
		ErrorDialog.openError(shell, title, dialogMessage, status);
	}
	
}