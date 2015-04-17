/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package _org.eclipse.dltk.internal.ui.text;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.ISourceModule;

/**
 * Interface of an object listening to script reconciling.
 */
public interface IScriptReconcilingListener {

	/**
	 * Called before reconciling is started.
	 */
	void aboutToBeReconciled();

	/**
	 * Called after reconciling has been finished.
	 * 
	 * @param ast
	 *            the compilation unit AST or <code>null</code> if the working
	 *            copy was consistent or reconciliation has been cancelled
	 * @param forced
	 *            <code>true</code> iff this reconciliation was forced
	 * @param progressMonitor
	 *            the progress monitor
	 */
	void reconciled(ISourceModule module, boolean forced,
			IProgressMonitor progressMonitor);
}
