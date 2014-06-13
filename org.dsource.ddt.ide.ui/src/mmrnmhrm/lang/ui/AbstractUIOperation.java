/*******************************************************************************
 * Copyright (c) 2014, 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.lang.ui;

import static melnorme.utilbox.core.Assert.AssertNamespace.assertTrue;
import mmrnmhrm.ui.actions.UIUserInteractionsHelper;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Display;


public abstract class AbstractUIOperation extends UIUserInteractionsHelper {
	
	protected final String operationName;
	
	public AbstractUIOperation(String operationName) {
		this.operationName = operationName;
	}
	
	public void executeSafe() {
		assertTrue(Display.getCurrent() != null);
		
		try {
			doExecute();
		} catch (CoreException ce) {
			OperationExceptionHandler.handle(ce, operationName, MSG_ERROR_EXECUTING_OPERATION);
		} catch (RuntimeException re) {
			OperationExceptionHandler.handle(re, operationName, MSG_INTERNAL_ERROR_EXECUTING_OPERATION);
		}
	}
	
	protected abstract Object doExecute() throws CoreException;
	
}