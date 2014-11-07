/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     xored software, Inc. - initial API and implementation
 *     xored software, Inc. - fix tab handling (Bug# 200024) (Alex Panchenko) 
 *******************************************************************************/
package _org.eclipse.dltk.internal.ui.editor;

import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;

/**
 * Internal implementation class for a change listener.
 */
public abstract class AbstractSelectionChangedListener implements ISelectionChangedListener {
		/**
	 * Installs this selection changed listener with the given selection
	 * provider. If the selection provider is a post selection provider,
	 * post selection changed events are the preferred choice, otherwise
	 * normal selection changed events are requested.
	 * 
	 * @param selectionProvider
	 */
	public void install(ISelectionProvider selectionProvider) {
		if (selectionProvider == null)
			return;
		if (selectionProvider instanceof IPostSelectionProvider) {
			IPostSelectionProvider provider = (IPostSelectionProvider) selectionProvider;
			provider.addPostSelectionChangedListener(this);
		} else {
			selectionProvider.addSelectionChangedListener(this);
		}
	}
	
	/**
	 * Removes this selection changed listener from the given selection provider.
	 */
	public void uninstall(ISelectionProvider selectionProvider) {
		if (selectionProvider == null)
			return;
		if (selectionProvider instanceof IPostSelectionProvider) {
			IPostSelectionProvider provider = (IPostSelectionProvider) selectionProvider;
			provider.removePostSelectionChangedListener(this);
		} else {
			selectionProvider.removeSelectionChangedListener(this);
		}
	}
}