/*******************************************************************************
 * Copyright (c) 2013, 2013 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package mmrnmhrm.core.build;

import java.io.IOException;


import melnorme.utilbox.concurrency.ExternalProcessLineReader;
import melnorme.utilbox.misc.StringUtil;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;

@Deprecated
public abstract class ExternalProcessLineNotifyHandler_Ext extends ExternalProcessLineReader {
	
	protected final IProgressMonitor monitor;
	@Deprecated
	public ExternalProcessLineNotifyHandler_Ext(ProcessBuilder pb, IProgressMonitor monitor) throws IOException {
		super(pb, StringUtil.UTF8);
		this.monitor = monitor;
	}
	
	@Override
	public int awaitTermination(int timeoutMs) throws InterruptedException {
		try {
			return super.awaitTermination(timeoutMs);
		} catch (InterruptedException e) {
			if(isCanceled()) {
				throw new OperationCanceledException();
			}
			throw e;
		}
	}
	
	@Override
	protected boolean isCanceled() {
		return monitor.isCanceled();
	}
	
}