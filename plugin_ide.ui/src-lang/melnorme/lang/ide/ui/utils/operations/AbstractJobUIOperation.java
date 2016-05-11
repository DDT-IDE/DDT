/*******************************************************************************
 * Copyright (c) 2014 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package melnorme.lang.ide.ui.utils.operations;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;

import melnorme.lang.ide.core.utils.EclipseUtils;
import melnorme.utilbox.concurrency.OperationCancellation;
import melnorme.utilbox.core.CommonException;

public abstract class AbstractJobUIOperation extends AbstractUIOperation {
	
	public AbstractJobUIOperation(String operationName) {
		super(operationName);
	}
	
	@Override
	protected void doOperation() throws CommonException, OperationCancellation {
		
		Display display = Display.getCurrent();
		
		new Job(getOperationName()) {
			
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					runBackgroundComputation(EclipseUtils.om(monitor));
					
					display.asyncExec(() -> handleComputationResult_handled());
					
				} catch(CommonException ce) {
					display.asyncExec(() -> handleError(ce));
				} catch(OperationCancellation e) {
					return Status.CANCEL_STATUS;
				}
				return Status.OK_STATUS;
				
			}
			
		}
		.schedule();
	}
	
	/* -----------------  ----------------- */
	
	protected void handleComputationResult_handled()  {
		try {
			handleComputationResult();
		} catch(CommonException ce) {
			handleError(ce);
		}
	}
	
}