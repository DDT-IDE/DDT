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
import org.eclipse.jface.text.information.IInformationPresenter;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.widgets.Composite;

import _org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;

public class ScriptSourceViewer extends LangSourceViewer {

	/**
	 * Text operation code for requesting the outline for the current input.
	 */
	public static final int SHOW_OUTLINE = 51;

	/**
	 * Text operation code for requesting the outline for the element at the
	 * current position.
	 */
	public static final int OPEN_STRUCTURE = 52;

	/**
	 * Text operation code for requesting the hierarchy for the current input.
	 */
	public static final int SHOW_HIERARCHY = 53;

	private IInformationPresenter fOutlinePresenter;
	private IInformationPresenter fStructurePresenter;
	private IInformationPresenter fHierarchyPresenter;

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
	public void doOperation(int operation) {
		if (getTextWidget() == null)
			return;

		switch (operation) {
		case SHOW_OUTLINE:
			if (fOutlinePresenter != null)
				fOutlinePresenter.showInformation();
			return;
		case OPEN_STRUCTURE:
			if (fStructurePresenter != null)
				fStructurePresenter.showInformation();
			return;
		case SHOW_HIERARCHY:
			if (fHierarchyPresenter != null)
				fHierarchyPresenter.showInformation();
			return;
		}

		super.doOperation(operation);
	}

	@Override
	public boolean canDoOperation(int operation) {
		if (operation == SHOW_OUTLINE)
			return fOutlinePresenter != null;
		if (operation == OPEN_STRUCTURE)
			return fStructurePresenter != null;
		if (operation == SHOW_HIERARCHY)
			return fHierarchyPresenter != null;

		return super.canDoOperation(operation);
	}

	@Override
	protected void configure_beforeViewerColors(SourceViewerConfiguration configuration) {
		super.configure_beforeViewerColors(configuration);
		
		if (configuration instanceof ScriptSourceViewerConfiguration) {
			ScriptSourceViewerConfiguration dltkSVCconfiguration = (ScriptSourceViewerConfiguration) configuration;
			fOutlinePresenter = dltkSVCconfiguration.getOutlinePresenter(this, false);
			if (fOutlinePresenter != null)
				fOutlinePresenter.install(this);

			fStructurePresenter = dltkSVCconfiguration.getOutlinePresenter(this, true);
			if (fStructurePresenter != null)
				fStructurePresenter.install(this);

			fHierarchyPresenter = dltkSVCconfiguration.getHierarchyPresenter(this, true);
			
			if (fHierarchyPresenter != null)
				fHierarchyPresenter.install(this);
			
		}
	}


	@Override
	public void unconfigure() {
		if (fOutlinePresenter != null) {
			fOutlinePresenter.uninstall();
			fOutlinePresenter = null;
		}
		if (fStructurePresenter != null) {
			fStructurePresenter.uninstall();
			fStructurePresenter = null;
		}
		if (fHierarchyPresenter != null) {
			fHierarchyPresenter.uninstall();
			fHierarchyPresenter = null;
		}

		super.unconfigure();
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
