/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *
 * Contributors:
 *     		IBM Corporation - initial API and implementation
 * 			Alex Panchenko <alex@xored.com>
 *******************************************************************************/

package _org.eclipse.dltk.internal.ui.editor;

import melnorme.lang.ide.ui.editor.LangSourceViewer;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.swt.widgets.Composite;

public class ScriptSourceViewer extends LangSourceViewer {

	// /**
	// * The backspace manager of this viewer.
	// *
	// */
	// private SmartBackspaceManager fBackspaceManager;

	public ScriptSourceViewer(Composite parent, IVerticalRuler verticalRuler,
			IOverviewRuler overviewRuler, boolean showAnnotationsOverview,
			int styles, IPreferenceStore store) {
		super(parent, verticalRuler, overviewRuler, showAnnotationsOverview, styles, store);
	}

	@Override
	public void resetVisibleRegion() {
		super.resetVisibleRegion();
		// re-enable folding if ProjectionViewer failed to due so
		// TODO: Add editor folding option here.
		if (fPreferenceStore != null && !isProjectionMode())
			enableProjection();
		/*
		 * && fPreferenceStore.getBoolean(PreferenceConstants.
		 * EDITOR_FOLDING_ENABLED)
		 */
	}

	void setReconciler(IReconciler reconciler) {
		fReconciler = reconciler;
	}

	IReconciler getReconciler() {
		return fReconciler;
	}
	
}